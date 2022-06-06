package org.oopscraft.apps.module.core.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.core.data.PageRowBounds;
import org.oopscraft.apps.module.core.sample.dao.SampleMapper;
import org.oopscraft.apps.module.core.sample.dao.SampleRepository;
import org.oopscraft.apps.module.core.sample.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SampleService {

    private final SampleRepository sampleRepository;

    private final SampleMapper sampleMapper;

    private final EntityManager entityManager;

    /**
     * getSamples
     * @param id
     * @param name
     * @param pageRequest
     * @return
     */
    public List<Sample> getSamples(String id, String name, PageRequest pageRequest) {

        // where clause
        Specification<SampleEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(id != null) {
                predicates.add(builder.equal(root.get(SampleEntity_.ID), id.concat("%")));
            }
            if(name != null) {
                predicates.add(builder.equal(root.get(SampleEntity_.NAME), name.concat("%")));
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        // sort clause
        Sort sort = Sort.by(Sort.Order.desc(SampleEntity_.MODIFY_DATE_TIME), Sort.Order.asc(SampleEntity_.ID));
        pageRequest.setSort(sort);

        // retrieves
        Page<SampleEntity> samplePage = sampleRepository.findAll(specification, pageRequest);

        // converts model
        ModelMapper modelMapper = new ModelMapper();
        List<Sample> samples = new ArrayList<>();
        samplePage.getContent().forEach(sampleEntity -> {
            Sample sample = modelMapper.map(sampleEntity, Sample.class);
            sample.setBackupId("");
            sample.setBackupName("");
            samples.add(sample);
        });

        // set total count
        pageRequest.setTotalCount(samplePage.getTotalElements());

        // returns
        return samples;
    }

    /**
     * getSamplesWithQueryDsl
     * @param id
     * @param name
     * @param pageRequest
     * @return
     */
    public List<Sample> getSamplesWithQueryDsl(String id, String name, PageRequest pageRequest) {

        // defines query dsl
        JPAQuery<Sample> query = new JPAQuery<>(entityManager);
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        QSampleBackupEntity qSampleBackupEntity = QSampleBackupEntity.sampleBackupEntity;
        query.select(Projections.fields(Sample.class,
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
        if(id != null){
            query.where(qSampleEntity.id.like(id.concat("%")));
        }
        if(name != null){
            query.where(qSampleEntity.name.like(id.concat("%")));
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
     * @param id
     * @param name
     * @param pageRequest
     * @return
     */
    public List<Sample> getSamplesWithMybatis(String id, String name, PageRequest pageRequest) {

        // retrieves
        PageRowBounds pageRowBounds = pageRequest.toPageRowBounds();
        List<SampleVo> sampleVos = sampleMapper.selectSamples(id, name, pageRowBounds);

        // covert model
        ModelMapper modelMapper = new ModelMapper();
        List<Sample> samples = new ArrayList<>();
        sampleVos.forEach(sampleVo -> {
            Sample sample = modelMapper.map(sampleVo, Sample.class);
            sample.setBackupId("");
            sample.setBackupName("");
            samples.add(sample);
        });

        // set total count
        pageRequest.setTotalCount(pageRowBounds.getTotalCount());

        // return
        return samples;
    }

    /**
     * getSample
     *
     * @param id
     */
    public Sample getSample(String id) {
        return sampleRepository.findById(id).map(sampleEntity->{
            return toSample(sampleEntity);
        }).orElse(null);
    }


    /**
     * toSample
     * @param sampleEntity
     * @return
     */
    private Sample toSample(SampleEntity sampleEntity) {
        ModelMapper modelMapper = new ModelMapper();
        Sample sample = modelMapper.map(sampleEntity, Sample.class);
        return sample.toBuilder()
                .backupId("")
                .backupName("")
                .build();
    }

    /**
     * saveSample
     *
     * @param sample
     */
    public void saveSample(Sample sample) {
        SampleEntity one = sampleRepository.findById(sample.getId()).orElse(null);
        if (one == null) {
            one = new SampleEntity();
            one.setId(sample.getId());
        }
        one.setId(sample.getId());
        one.setName(sample.getName());
        one.setNumber(sample.getNumber());
        one.setLongNumber(sample.getLongNumber());
        one.setDoubleNumber(sample.getDoubleNumber());
        one.setBigDecimal(sample.getBigDecimal());
        one.setSqlDate(sample.getSqlDate());
        one.setUtilDate(sample.getUtilDate());
        one.setTimestamp(sample.getTimestamp());
        one.setData(sample.getData());

        // save
        sampleRepository.saveAndFlush(one);
    }

    /**
     * deleteSample
     *
     * @param id
     */
    public void deleteSample(String id) {
        sampleRepository.deleteById(id);
        sampleRepository.flush();
    }

}

