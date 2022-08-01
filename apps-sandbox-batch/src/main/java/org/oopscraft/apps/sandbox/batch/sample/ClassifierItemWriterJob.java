package org.oopscraft.apps.sandbox.batch.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.ClassifierItemWriter;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.*;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleItemBackupRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@BatchComponentScan
@RequiredArgsConstructor
public class ClassifierItemWriterJob extends AbstractJob {

    private long size;

    private String filePath;

    private final SampleBackupRepository sampleBackupRepository;

    private final SampleItemBackupRepository sampleItemBackupRepository;

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

        // 2. classifier writer step
        addStep(classifierItemWriterStep());

    }

    /**
     * classifier composite item writer
     * 여러개의 item writer 를 동시 호출
     * @return
     */
    private Step classifierItemWriterStep() {
        return stepBuilderFactory.get("compositeItemWriterStep")
                .<SampleVo,SampleVo>chunk(10)
                .reader(dbItemReader())
                .writer(classifierItemWriter())
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
     * classifier item writer
     * @return
     */
    private ClassifierItemWriter<SampleVo> classifierItemWriter() {
        ItemWriter<SampleVo> dbItemWriter = dbItemWriter();
        ItemWriter<SampleVo> fileItemWriter = fileItemWriter();
        return ClassifierItemWriter.<SampleVo>builder()
                .delegate(dbItemWriter)
                .delegate(fileItemWriter)
                .classifier((Classifier<SampleVo, ItemWriter<? super SampleVo>>) sampleVo -> {
                    if (sampleVo.getNumber() % 2 == 1) {
                        return dbItemWriter;
                    } else {
                        return fileItemWriter;
                    }
                }).build();
   }

    /**
     * custom writer
     * @return
     */
    public ItemWriter<SampleVo> dbItemWriter() {
        return items -> {
            for(SampleVo item : items) {
                SampleBackupEntity one = sampleBackupRepository.findById(item.getId()).orElse(null);
                if(one == null){
                    one = SampleBackupEntity.builder()
                            .id(item.getId())
                            .build();
                }
                one.setName(item.getName());
                one.setNumber(item.getNumber());
                one.setLongNumber(item.getLongNumber());
                one.setDoubleNumber(item.getDoubleNumber());
                one.setBigDecimal(item.getBigDecimal());
                one.setSqlDate(item.getSqlDate());
                one.setUtilDate(item.getUtilDate());
                one.setTimestamp(item.getTimestamp());
                one.setLocalDate(item.getLocalDate());
                one.setLocalDateTime(item.getLocalDateTime());
                one.setLobText(item.getLobText());
                one.setCryptoText(item.getCryptoText());
                sampleBackupRepository.saveAndFlush(one);

                // copy child items
                List<SampleItemEntity> sampleItemEntities = jpaQueryFactory
                        .selectFrom(QSampleItemEntity.sampleItemEntity)
                        .where(QSampleItemEntity.sampleItemEntity.sampleId.eq(item.getId()))
                        .fetch();
                List<SampleItemBackupEntity> sampleItemBackupEntities = new ArrayList<>();
                sampleItemEntities.forEach(sampleItemEntity -> {
                    SampleItemBackupEntity sampleItemBackupEntity = modelMapper.map(sampleItemEntity, SampleItemBackupEntity.class);
                    sampleItemBackupEntities.add(sampleItemBackupEntity);
                });
                jpaQueryFactory.delete(QSampleItemBackupEntity.sampleItemBackupEntity)
                        .where(QSampleItemBackupEntity.sampleItemBackupEntity.sampleId.eq(item.getId()))
                        .execute();
                sampleItemBackupRepository.saveAllAndFlush(sampleItemBackupEntities);
            }
        };
    }

    /**
     * file item writer
     *
     * @return
     */
    public DelimiterFileItemWriter<SampleVo> fileItemWriter() {
        return createDelimiterFileItemWriterBuilder(SampleVo.class)
                .name("fileItemWriter")
                .filePath(filePath)
                .build();
    }

}
