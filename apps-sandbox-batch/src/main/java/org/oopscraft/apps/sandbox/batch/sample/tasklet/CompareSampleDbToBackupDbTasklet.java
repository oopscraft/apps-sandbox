package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleRepository;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleEntity;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
@Builder
public class CompareSampleDbToBackupDbTasklet extends AbstractTasklet {

    @Builder.Default
    private boolean includeItem = false;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private SampleBackupRepository sampleBackupRepository;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // 1. 건수 확인
        long sampleCount = sampleRepository.count();
        long sampleBackupCount = sampleBackupRepository.count();
        log.info("== sampleCount:{}", sampleCount);
        log.info("== sampleBackupCount:{}", sampleBackupCount);
        Assert.isTrue(sampleCount == sampleBackupCount, String.format("sampleCount[%d], sampleBackupCount[%d] is mismatched.", sampleCount, sampleBackupCount));

        // 2. 컬럼 내용 확인
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        jpaQueryFactory.selectFrom(QSampleEntity.sampleEntity).stream().forEach(item ->{
            try {
                SampleEntity sampleEntity = sampleRepository.findById(item.getId()).orElse(null);
                SampleBackupEntity sampleBackupEntity = sampleBackupRepository.findById(item.getId()).orElse(null);
                sampleEntity.setModifyDateTime(null);
                sampleBackupEntity.setModifyDateTime(null);
                String sampleVoToString = objectMapper.writeValueAsString(sampleEntity);
                String sampleBackupVoToString = objectMapper.writeValueAsString(sampleBackupEntity);
                log.debug("== sampleVoToString\t\t:{}", sampleVoToString);
                log.debug("== sampleBackupVoToString\t:{}", sampleBackupVoToString);
                Assert.isTrue(sampleVoToString.equals(sampleBackupVoToString), "data mismatch.");
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        });

        // 3. sample item vs sample item backup
        if(includeItem) {

        }
    }

}
