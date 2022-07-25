package org.oopscraft.apps.sandbox.batch.sample;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;

import static org.junit.jupiter.api.Assertions.*;

class FileToDbWithConfigurableToCustomJobTest extends AbstractJobTestSupport {

    @Test
    public void testDefault() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(FileToDbWithConfigurableToCustomJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("size","123")
                .build();
        launchJob(batchContext);
    }

}