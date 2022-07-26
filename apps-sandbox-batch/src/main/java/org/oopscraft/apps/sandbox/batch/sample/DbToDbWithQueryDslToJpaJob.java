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
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
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
public class DbToDbWithQueryDslToJpaJob extends AbstractJob {

    private long size;

    /**
     * initialize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // parameter
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleDbTasklet.builder().size(size).build());

        // 2. 데이터 처리 (Query DSL reader -> custom writer)
        addStep(doToDbStep());

        // 3. 결과 검증
        addStep(CompareSampleDbToBackupDbTasklet.builder().build());
    }

    /**
     * do to do step
     * @return
     */
    public Step doToDbStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo,SampleBackupEntity>chunk(10)
                .reader(dbItemReader())
                .processor(convertProcessor())
                .writer(dbItemWriter())
                .build();
    }

    /**
     * query DSL reader
     * @return
     */
    public QueryDslDbItemReader<SampleVo> dbItemReader() {
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
        ModelMapper modelMapper = new ModelMapper();
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
    public JpaDbItemWriter<SampleBackupEntity> dbItemWriter() {
        return createJpaDbItemWriterBuilder(SampleBackupEntity.class)
                .name("jpaWriter")
                .build();
    }

}
