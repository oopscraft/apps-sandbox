package org.oopscraft.apps.sample.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTest;

@Slf4j
public class TransactionManuallyJobTest extends AbstractJobTest {

    /**
     * test default
     */
    @Test
    public void testDefault() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(TransactionManuallyJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("limit", "1234");
        launchJob(batchContext);
    }

}
