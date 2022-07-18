package org.oopscraft.apps.sandbox.batch.dbtodb;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;
import org.oopscraft.apps.sandbox.batch.sample.job.dbtodb.MybatisReaderToJpaWriterJob;

@Slf4j
public class MybatisReaderToJpaWriterJobTest extends AbstractJobTestSupport {

    @Test
    public void testDefault() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(MybatisReaderToJpaWriterJob.class)
                .baseDate(getCurrentBaseDate())
                .jobParameter("size", "123")
                .build();
        launchJob(batchContext);

    }

}
