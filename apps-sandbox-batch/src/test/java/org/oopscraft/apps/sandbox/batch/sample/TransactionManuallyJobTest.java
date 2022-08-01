package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;
import org.oopscraft.apps.sandbox.batch.sample.TransactionManuallyJob;

@Slf4j
public class TransactionManuallyJobTest extends AbstractJobTestSupport {

    /**
     * test default
     */
    @Test
    public void testDefault() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(TransactionManuallyJob.class)
                .baseDate("20210101")
                .jobParameter("size", "123")
                .build();
        launchJob(batchContext);
    }

}
