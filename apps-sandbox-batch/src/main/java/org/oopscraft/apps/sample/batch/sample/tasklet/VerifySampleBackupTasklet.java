package org.oopscraft.apps.sample.batch.sample.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.tasklet.AbstractTasklet;
import org.oopscraft.apps.sample.batch.sample.dao.SampleBackupMapper;
import org.oopscraft.apps.sample.batch.sample.dao.SampleMapper;
import org.oopscraft.apps.sample.batch.sample.dto.SampleBackupVo;
import org.oopscraft.apps.sample.batch.sample.dto.SampleVo;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
@Builder
public class VerifySampleBackupTasklet extends AbstractTasklet {

    @Autowired
    private SampleMapper sampleMapper;

    @Autowired
    private SampleBackupMapper sampleBackupMapper;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // checks count
        checkCount();

        // checks field value
        checkFieldValue();
    }

    /**
     * checkCount
     */
    private void checkCount() {
        int sampleCount = sampleMapper.selectSampleCount();
        int sampleBackupCount = sampleBackupMapper.selectSampleBackupCount();
        log.info("== sampleCount:{}", sampleCount);
        log.info("== sampleBackupCount:{}", sampleBackupCount);
        Assert.isTrue(sampleCount == sampleBackupCount, String.format("sampleCount[%d], sampleBackupCount[%d] is mismatched.", sampleCount, sampleBackupCount));
    }

    /**
     * checkFieldValue
     */
    private void checkFieldValue() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Cursor<SampleVo> cursor = sampleMapper.selectSamples(Integer.MAX_VALUE);
        cursor.forEach(item -> {
            try {
                String id = item.getId();
                SampleVo sampleVo = sampleMapper.selectSample(id);
                SampleBackupVo sampleBackupVo = sampleBackupMapper.selectSampleBackup(id);
                String sampleVoToString = objectMapper.writeValueAsString(sampleVo);
                String sampleBackupVoToString = objectMapper.writeValueAsString(sampleBackupVo);
                log.debug("== sampleVoToString\t\t:{}", sampleVoToString);
                log.debug("== sampleBackupVoToString\t:{}", sampleBackupVoToString);
                Assert.isTrue(sampleVoToString.equals(sampleBackupVoToString), "data mismatch.");
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        });
    }

}
