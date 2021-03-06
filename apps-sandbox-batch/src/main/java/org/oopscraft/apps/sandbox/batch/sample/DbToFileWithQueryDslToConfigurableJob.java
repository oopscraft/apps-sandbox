package org.oopscraft.apps.sandbox.batch.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleItemVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleItemEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleItemEntity;
import org.springframework.batch.core.Step;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@BatchComponentScan
public class DbToFileWithQueryDslToConfigurableJob extends AbstractJob {

    private long size;

    private String filePath;

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

        // defines
        filePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.dat", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleDbTasklet.builder().size(size).build());

        // 2. 데이터 처리
        addStep(dbToFileStep());
    }

    /**
     * db to file step
     * @return
     */
    public Step dbToFileStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo, SampleVo>chunk(10)
                .reader(dbItemReader())
                .writer(fileItemWriter())
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
                .name("dbItemReader")
                .query(query)
                .build();
    }

    /**
     * db writer
     * @return
     */
    public DelimiterFileItemWriter<SampleVo> fileItemWriter() {
        DelimiterFileItemWriter<SampleVo> fileWriter = new DelimiterFileItemWriter.Configurable<>() {
            @Override
            public void internalWrite(List<? extends SampleVo> items) throws Exception {
                for(SampleVo sampleVo : items) {

                    // writes sample
                    write(sampleVo);

                    // select sample items
                    QSampleItemEntity qSampleItemEntity = QSampleItemEntity.sampleItemEntity;
                    List<SampleItemEntity> sampleItemEntities = jpaQueryFactory
                            .selectFrom(qSampleItemEntity)
                            .where(qSampleItemEntity.sampleId.eq(sampleVo.getId()))
                            .fetch();
                    for(SampleItemEntity sampleItemEntity : sampleItemEntities) {
                        SampleItemVo sampleItemVo = SampleItemVo.builder()
                                .sampleId(sampleItemEntity.getSampleId())
                                .id(sampleItemEntity.getId())
                                .build();
                        modelMapper.map(sampleItemEntity, sampleItemVo);

                        // writes sample item
                        write(sampleItemVo);
                    }
                }
            }
        };
        fileWriter.setName("fileItemWriter");
        fileWriter.setItemType(SampleVo.class);
        fileWriter.setFilePath(filePath);
        return fileWriter;
    }

}
