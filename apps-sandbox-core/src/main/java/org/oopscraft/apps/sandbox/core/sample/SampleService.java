package org.oopscraft.apps.sandbox.core.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.core.data.PageRowBounds;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleErrorRepository;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleMapper;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleRepository;
import org.oopscraft.apps.sandbox.core.sample.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SampleService {

    public static enum DaoType { JPA, QUERY_DSL, MYBATIS }

    private final SampleRepository sampleRepository;

    private final SampleBackupRepository sampleBackupRepository;

    private final SampleErrorRepository sampleErrorRepository;

    private final JPAQueryFactory jpaQueryFactory;

    private final SampleMapper sampleMapper;

    private final PlatformTransactionManager transactionManager;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * getSamples
     * @param sampleSearch
     * @param daoType
     * @param pageRequest
     * @return
     */
    public List<Sample> getSamples(SampleSearch sampleSearch, DaoType daoType, PageRequest pageRequest) {
        switch(daoType) {
            case JPA:
                return getSamplesWithJpa(sampleSearch, pageRequest);
            case QUERY_DSL:
                return getSamplesWithQueryDsl(sampleSearch, pageRequest);
            case MYBATIS:
                return getSamplesWithMybatis(sampleSearch, pageRequest);
            default:
                throw new RuntimeException(String.format("Invalid DaoType[%s]", daoType));
        }
    }

    /**
     * getSamplesWithJpa
     * @param sampleSearch
     * @param pageRequest
     * @return
     */
    public List<Sample> getSamplesWithJpa(SampleSearch sampleSearch, PageRequest pageRequest) {

        // where clause
        Specification<SampleEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(sampleSearch.getId() != null) {
                predicates.add(builder.equal(root.get(SampleEntity_.ID), sampleSearch.getId().concat("%")));
            }
            if(sampleSearch.getName() != null) {
                predicates.add(builder.equal(root.get(SampleEntity_.NAME), sampleSearch.getName().concat("%")));
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        // sort clause
        Sort sort = Sort.by(Sort.Order.desc(SampleEntity_.MODIFY_DATE_TIME), Sort.Order.asc(SampleEntity_.ID));
        pageRequest.setSort(sort);

        // retrieves
        Page<SampleEntity> samplePage = sampleRepository.findAll(specification, pageRequest);

        // returns
        List<Sample> samples = samplePage.getContent().stream()
                .map(Sample::from)
                .collect(Collectors.toList());
        pageRequest.setTotalCount(samplePage.getTotalElements());
        return samples;
    }

    /**
     * getSamplesWithQueryDsl
     * @param sampleSearch
     * @param pageRequest
     * @return
     */
    public List<Sample> getSamplesWithQueryDsl(SampleSearch sampleSearch, PageRequest pageRequest) {

        // defines query dsl
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        QSampleBackupEntity qSampleBackupEntity = QSampleBackupEntity.sampleBackupEntity;
        JPAQuery<Sample> query = jpaQueryFactory
                .select(Projections.fields(Sample.class,
                        qSampleEntity.id,
                        qSampleEntity.name,
                        qSampleEntity.number,
                        qSampleEntity.longNumber,
                        qSampleEntity.doubleNumber,
                        qSampleEntity.bigDecimal,
                        qSampleEntity.sqlDate,
                        qSampleEntity.utilDate,
                        qSampleEntity.timestamp,
                        qSampleEntity.data,
                        qSampleBackupEntity.id.as("backupId"),
                        qSampleBackupEntity.name.as("backupName")
                )).from(qSampleEntity)
                .leftJoin(qSampleBackupEntity)
                .on(qSampleEntity.id.eq(qSampleBackupEntity.id));

        // where condition
        if(sampleSearch.getId() != null){
            query.where(qSampleEntity.id.like(sampleSearch.getId().concat("%")));
        }
        if(sampleSearch.getName() != null){
            query.where(qSampleEntity.name.like(sampleSearch.getName().concat("%")));
        }

        // pagination
        query.offset(pageRequest.getOffset()).limit(pageRequest.getPageSize());

        // fetch
        List<Sample> samples = query.fetch();

        // set total count
        pageRequest.setTotalCount(query.fetchCount());

        // returns
        return samples;
    }

    /**
     * getSamplesWithMybatis
     * @param sampleSearch sampleSearch
     * @param pageRequest pageRequest
     * @return
     */
    public List<Sample> getSamplesWithMybatis(SampleSearch sampleSearch, PageRequest pageRequest) {
        PageRowBounds pageRowBounds = pageRequest.toPageRowBounds();
        List<Sample> samples = sampleMapper.selectSamples(sampleSearch.getId(), sampleSearch.getName(), pageRowBounds).stream()
                .map(Sample::from)
                .collect(Collectors.toList());
        pageRequest.setTotalCount(pageRowBounds.getTotalCount());
        return samples;
    }

    /**
     * getSample
     * @param id
     */
    public Sample getSample(@NotNull String id) {
        return sampleRepository.findById(id).map(Sample::from).orElse(null);
    }

    /**
     * saveSample
     * @param sample
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void saveSample(@NotNull Sample sample, boolean forceException, boolean ignoreException) {
        try {
            // saves sample
            saveSample(sample, false);

            // saves sample backup
            SampleBackup sampleBackup = modelMapper.map(sample, SampleBackup.class);
            saveSampleBackup(sampleBackup, forceException);

        }catch(Exception e){
            if(ignoreException) {
                SampleError sampleError = modelMapper.map(sample, SampleError.class);
                saveSampleError(sampleError, false);
            }else {
                throw new RuntimeException("Force To Fail");
            }
        }
    }

    /**
     * save sample
     * @param sample
     * @param forceException
     */
    @Transactional
    public void saveSample(@NotNull Sample sample, boolean forceException) {
        SampleEntity one = sampleRepository.findById(sample.getId()).orElse(null);
        if (one == null) {
            one = SampleEntity.builder()
                    .id(sample.getId())
                    .build();
        }
        modelMapper.map(sample, one);
        if(forceException) {
            one.setId(null);
        }
        sampleRepository.saveAndFlush(one);
    }


    /**
     * saves sample backup
     * @param sampleBackup
     */
    @Transactional
    public void saveSampleBackup(@NotNull SampleBackup sampleBackup, boolean forceException) {
        SampleBackupEntity one = sampleBackupRepository.findById(sampleBackup.getId()).orElse(null);
        if(one == null) {
            one = SampleBackupEntity.builder()
                    .id(sampleBackup.getId())
                    .build();
        }
        modelMapper.map(sampleBackup, one);
        if(forceException){
            one.setId(null);
        }
        sampleBackupRepository.saveAndFlush(one);
    }

    /**
     * saveSampleError
     * @param sampleError
     */
    @Transactional
    public void saveSampleError(@NotNull SampleError sampleError, boolean forceException) {
        SampleErrorEntity one = sampleErrorRepository.findById(sampleError.getId()).orElse(null);
        if(one == null) {
            one = SampleErrorEntity.builder()
                    .id(sampleError.getId())
                    .build();
        }
        modelMapper.map(sampleError, one);
        if(forceException) {
            one.setId(null);
        }
        sampleErrorRepository.saveAndFlush(one);
    }

    /**
     * deleteSample
     *
     * @param id
     */
    @Transactional
    public void deleteSample(@NotNull String id) {
        sampleRepository.deleteById(id);
        sampleRepository.flush();
    }

    /**
     * returns total count of sample
     * @return
     */
    public long getSampleTotalCount() {
        return sampleRepository.count();
    }

    /**
     * returns total count of sample backup count
     * @return
     */
    public long getSampleBackupTotalCount() {
        return sampleBackupRepository.count();
    }

    /**
     * returns total count of sample error data
     * @return
     */
    public long getSampleErrorTotalCount() {
        return sampleErrorRepository.count();
    }

}

