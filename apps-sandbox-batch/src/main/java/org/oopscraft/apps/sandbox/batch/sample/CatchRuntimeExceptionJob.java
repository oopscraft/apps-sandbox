package org.oopscraft.apps.sandbox.batch.sample;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.core.data.TransactionTemplateUtils;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleErrorEntity;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleErrorRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.transaction.annotation.Propagation;

import java.util.Optional;

@Slf4j
@BatchComponentScan
@RequiredArgsConstructor
public class CatchRuntimeExceptionJob extends AbstractJob {

    private long size;

    private final SampleRepository sampleRepository;

    private final SampleBackupRepository sampleBackupRepository;

    private final SampleErrorRepository sampleErrorRepository;

    private long count = 0;

    /**
     * initialize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // parameter
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value -> Long.parseLong(value))
                .orElseThrow(()-> new RuntimeException("invalid size"));

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleDbTasklet.builder().size(size).build());

        // 2. catch runtime exception step
        addStep(catchRuntimeExceptionStep());

        // 3. verifies sample backup data
        addStep(verifyStep());

    }

    /**
     * catch runtime exception step
     * @return
     */
    public Step catchRuntimeExceptionStep() {
        return stepBuilderFactory.get("step")
                .<SampleEntity, SampleEntity>chunk(1)
                .reader(itemDbReader())
                .writer(itemDbWriter())
                .build();
    }

    /**
     * item db reader
     * @return
     */
    public ItemReader<SampleEntity> itemDbReader() {
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<SampleEntity> query = jpaQueryFactory
                .select(qSampleEntity)
                .from(qSampleEntity)
                .limit(size);
        return createQueryDslDbItemReader(SampleEntity.class)
                .name("itemDbReader")
                .query(query)
                .build();
    }

    /**
     * itemWriter
     * @return
     */
    public ItemWriter<SampleEntity> itemDbWriter() {
        return items -> {
            for(SampleEntity item : items) {

                // (*) new transaction
                TransactionTemplateUtils.executeWithoutResult(transactionManager, Propagation.REQUIRES_NEW, transactionStatus -> {
                    try {
                        count ++;
                        log.debug("sampleEntity: {}", item);
                        SampleBackupEntity sampleBackupEntity = SampleBackupEntity.builder()
                                .id(item.getId())
                               .build();
                        modelMapper.map(item, sampleBackupEntity);

                        // force to error
                        if(count%2 == 0) {
                            sampleBackupEntity.setId(null); // force to error
                        }
                        sampleBackupRepository.saveAndFlush(sampleBackupEntity);

                    }catch(Exception ignore){
                        log.warn(ignore.getMessage());

                        // checks rollback-only manually (jpa transaction manager not support nested transaction)
                        transactionStatus.setRollbackOnly();

                        // (*) writes error with new transaction
                        TransactionTemplateUtils.executeWithoutResult(transactionManager, Propagation.REQUIRES_NEW, transactionStatus1->{
                            SampleErrorEntity sampleErrorEntity = modelMapper.map(item, SampleErrorEntity.class);
                            sampleErrorRepository.saveAndFlush(sampleErrorEntity);
                        });
                    }
               });
           }
        };
    }

    /**
     * verify step
     * @return
     */
    public Step verifyStep() {
        return getStepBuilderFactory().get("verifyStep")
                .tasklet((contribution, chunkContext) -> {

                    // gets row count
                    long sampleTotalCount = sampleRepository.count();
                    long sampleBackupTotalCount = sampleBackupRepository.count();
                    long sampleErrorTotalCount = sampleErrorRepository.count();
                    log.info("================================================");
                    log.info("| sampleTotalCount:{}", sampleTotalCount);
                    log.info("| sampleBackupTotalCount:{}", sampleBackupTotalCount);
                    log.info("| sampleErrorTotalCount:{}", sampleErrorTotalCount);
                    log.info("================================================");

                    // checks count
                    if(sampleTotalCount != (sampleBackupTotalCount + sampleErrorTotalCount)){
                        throw new RuntimeException("Count mismatched.");
                    }

                    // finish
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
