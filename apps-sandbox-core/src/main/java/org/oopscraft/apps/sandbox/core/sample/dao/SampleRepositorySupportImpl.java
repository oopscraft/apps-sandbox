package org.oopscraft.apps.sandbox.core.sample.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.sandbox.core.sample.dto.QSampleBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.dto.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.dto.SampleEntity;
import org.oopscraft.apps.sandbox.core.sample.dto.SampleVo;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;


@NoRepositoryBean
public class SampleRepositorySupportImpl extends QuerydslRepositorySupport implements SampleRepositorySupport {

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * @param domainClass must not be {@literal null}.
     */
    public SampleRepositorySupportImpl(Class<?> domainClass) {
        super(SampleEntity.class);
    }

    @Override
    public List<SampleVo> findSamples(String id, String name, PageRequest pageRequest) {
        // defines query dsl
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        QSampleBackupEntity qSampleBackupEntity = QSampleBackupEntity.sampleBackupEntity;
        JPAQuery<SampleVo> query = new JPAQuery<>(getEntityManager());
        query.select(Projections.fields(SampleVo.class,
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
            query.where(qSampleEntity.name.like(name.concat("%")));
        }

        // pagination
        query.offset(pageRequest.getOffset()).limit(pageRequest.getPageSize());

        // fetch
        QueryResults<SampleVo> queryResult = query.fetchResults();
        pageRequest.setTotalCount(queryResult.getTotal());
        return queryResult.getResults();
    }

}
