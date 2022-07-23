package org.oopscraft.apps.sandbox.batch.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.db.JpaDbItemWriter;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CompareSampleDbToBackupDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@BatchComponentScan
public class DbQueryDslToDbJpaJob extends AbstractJob {

    private long size;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * initailize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // 1. 테스트 데이터 생성
        addStep(new CreateSampleDbTasklet(size));

        // 2. 데이터 처리 (Query DSL reader -> custom writer)
        addStep(copySampleStep());

        // 3. 결과 검증
        addStep(new CompareSampleDbToBackupDbTasklet());
    }

    /**
     * 데이터 복사
     * @return
     */
    public Step copySampleStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo,SampleBackupEntity>chunk(10)
                .reader(queryDslReader())
                .processor(convertProcessor())
                .writer(jpaWriter())
                .build();
    }

    /**
     * query DSL reader
     * @return
     */
    public QueryDslDbItemReader<SampleVo> queryDslReader() {
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<SampleVo> query = jpaQueryFactory
                .select(Projections.fields(SampleVo.class,
                        qSampleEntity.id,
                        qSampleEntity.name,
                        qSampleEntity.number,
                        qSampleEntity.longNumber,
                        qSampleEntity.doubleNumber,
                        qSampleEntity.bigDecimal,
                        qSampleEntity.sqlDate,
                        qSampleEntity.utilDate,
                        qSampleEntity.timestamp,
                        qSampleEntity.localDate,
                        qSampleEntity.localDateTime,
                        qSampleEntity.lobText,
                        qSampleEntity.cryptoText
                )).from(qSampleEntity)
                .limit(size);
        return createQueryDslDbItemReader(SampleVo.class)
                .query(query)
                .build();
    }

    /**
     * convert processor
     * @return
     */
    public ItemProcessor<SampleVo, SampleBackupEntity> convertProcessor() {
        return item -> {
            SampleBackupEntity sampleBackupEntity = SampleBackupEntity.builder()
                    .id(item.getId())
                    .build();
            modelMapper.map(item, sampleBackupEntity);
            return sampleBackupEntity;
        };
    }

    /**
     * Jpa writer
     * @return
     */
    public JpaDbItemWriter<SampleBackupEntity> jpaWriter() {
        return createJpaDbItemWriterBuilder(SampleBackupEntity.class)
                .name("jpaWriter")
                .build();
    }

}