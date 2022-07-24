package org.oopscraft.apps.sandbox.batch.sample;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;

class DbToDbWithMybatisToMybatisJobTest extends AbstractJobTestSupport {

    @Test
    public void testDefault() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(DbToDbWithMybatisToMybatisJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("size", "123")
                .build();
        launchJob(batchContext);
    }

    @Test
    public void testLargeData() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(DbToDbWithMybatisToMybatisJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("size", "12345")
                .build();
        launchJob(batchContext);
    }
}