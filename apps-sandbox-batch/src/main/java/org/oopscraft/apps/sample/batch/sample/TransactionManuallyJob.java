package org.oopscraft.apps.sample.batch.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.batch.tasklet.AbstractTasklet;
import org.oopscraft.apps.sample.batch.sample.dao.SampleBackupMapper;
import org.oopscraft.apps.sample.batch.sample.dao.SampleMapper;
import org.oopscraft.apps.sample.batch.sample.dto.SampleBackupVo;
import org.oopscraft.apps.sample.batch.sample.dto.SampleVo;
import org.oopscraft.apps.sample.batch.sample.tasklet.CreateSampleTasklet;
import org.oopscraft.apps.sample.batch.sample.tasklet.VerifySampleBackupTasklet;
import org.oopscraft.apps.sandbox.core.sample.Sample;
import org.oopscraft.apps.sandbox.core.sample.SampleBackup;
import org.oopscraft.apps.sandbox.core.sample.SampleError;
import org.oopscraft.apps.sandbox.core.sample.SampleService;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleErrorRepository;
import org.oopscraft.apps.sandbox.core.sample.dto.*;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TransactionManuallyJob extends AbstractJob {

    private final SampleService sampleService;

    private ModelMapper modelMapper = new ModelMapper();

    public enum ReaderType { MYBATIS, QUERY_DSL, QUERY_DSL_PROJECTION }

    public enum WriterType { MYBATIS, JPA }

    /**
     * initialize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // parameter
        Assert.notNull(batchContext.getJobParameter("limit"), "limit must not be null");
        int limit = Integer.parseInt(batchContext.getJobParameter("limit"));

        // 1. creates sample data
        addTasklet(CreateSampleTasklet.builder()
                .limit(limit)
                .build());

        // 2. add step
        addStep(step(limit));

        // 3. verifies sample backup data
        addStep(verifyStep());

    }

    /**
     * step
     * @return
     */
    public Step step(int limit) {

        // defines transaction attribute
        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
        transactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        // builds step
        return stepBuilderFactory.get("step")
                .<SampleEntity, SampleEntity>chunk(1)
                .reader(itemReader(limit))
                .writer(itemWriter())
                .transactionAttribute(transactionAttribute)
                .build();
    }

    /**
     * itemReader
     * @param limit
     * @return
     */
    public ItemReader<SampleEntity> itemReader(int limit) {
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<SampleEntity> query = getJpaQueryFactory()
                .select(qSampleEntity)
                .from(qSampleEntity)
                .limit(limit);
        return createQueryDslDbItemReader(SampleEntity.class)
                .name("itemReader")
                .query(query)
                .build();
    }

    /**
     * itemWriter
     * @return
     */
    public ItemWriter<SampleEntity> itemWriter() {
        return sampleEntities -> {
            int index = 0;
            for(SampleEntity sampleEntity : sampleEntities) {
                index ++;
                log.debug("sampleEntity: {}", sampleEntity);
                SampleBackup sampleBackup = SampleBackup.builder()
                        .id(sampleEntity.getId())
                        .build();
                modelMapper.map(sampleEntity, sampleBackup);

                // force to error
                boolean forceToException = false;
                if(index%2 == 0) {
                    forceToException = true;
                }

                // manually transaction
                try {
                    sampleService.saveSampleBackup(sampleBackup, forceToException);
                }catch(Exception ignore){
                    log.warn(ignore.getMessage());
                    SampleError sampleError = modelMapper.map(sampleEntity, SampleError.class);
                    sampleService.saveSampleError(sampleError, false);
                }
            }
        };
    }

    /**
     * verify step
     * @return
     */
    public Step verifyStep() {
        Tasklet tasklet = (contribution, chunkContext) -> {
            log.info("== verify");
            long sampleTotalCount = sampleService.getSampleTotalCount();
            long sampleBackupTotalCount = sampleService.getSampleBackupTotalCount();
            long sampleErrorTotalCount = sampleService.getSampleErrorTotalCount();
            log.info("== sampleTotalCount:{}", sampleTotalCount);
            log.info("== sampleBackupTotalCount:{}", sampleBackupTotalCount);
            log.info("== sampleErrorTotalCount:{}", sampleErrorTotalCount);

            // checks count
            if(sampleTotalCount != (sampleBackupTotalCount + sampleErrorTotalCount)){
                throw new RuntimeException("Count mismatched.");
            }

            // returns
            return RepeatStatus.FINISHED;
        };
        return getStepBuilderFactory().get("verifyStep")
                .tasklet(tasklet)
                .build();
    }

}
