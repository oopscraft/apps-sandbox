package org.oopscraft.apps.sandbox.batch.bak;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;

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
                .jobParameter("limit", "1234")
                .build();
        launchJob(batchContext);
    }

}
