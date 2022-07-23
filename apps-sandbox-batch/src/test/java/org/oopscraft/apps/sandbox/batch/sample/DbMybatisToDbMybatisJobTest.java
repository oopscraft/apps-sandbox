package org.oopscraft.apps.sandbox.batch.sample;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;

import static org.junit.jupiter.api.Assertions.*;

class DbMybatisToDbMybatisJobTest extends AbstractJobTestSupport {

    @Test
    public void testDefault() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(DbMybatisToDbMybatisJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("limit", "123")
                .build();
        launchJob(batchContext);
    }

    @Test
    public void testLargeData() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(DbMybatisToDbMybatisJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("limit", "12345")
                .build();
        launchJob(batchContext);
    }
}