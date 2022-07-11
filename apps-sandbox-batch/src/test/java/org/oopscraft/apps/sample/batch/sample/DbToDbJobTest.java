package org.oopscraft.apps.sample.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTest;

@Slf4j
public class DbToDbJobTest extends AbstractJobTest {

    /**
     * Mybatis to Mybatis test
     */
    @Test
    public void testMybatisToMybatis() {
        BatchContext batchContext = BatchContext.builder()
                .jobClass(DbToDbJob.class)
                .baseDate("20210101")
                .jobParameter("readerType", DbToDbJob.ReaderType.MYBATIS.name())
                .jobParameter("writerType", DbToDbJob.ReaderType.MYBATIS.name())
                .jobParameter("limit", "123")
                .build();
        launchJob(batchContext);

    }

    /**
     * Mybatis to Mybatis test
     */
    @Test
    public void testMybatisToJpa() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(DbToDbJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("readerType", DbToDbJob.ReaderType.MYBATIS.name());
        batchContext.setJobParameter("writerType", DbToDbJob.WriterType.JPA.name());
        batchContext.setJobParameter("limit", "123");
        launchJob(batchContext);
    }

    /**
     * Query DSL to Mybatis test
     */
    @Test
    public void testQueryDslToMybatis() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(DbToDbJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL.name());
        batchContext.setJobParameter("writerType", DbToDbJob.WriterType.MYBATIS.name());
        batchContext.setJobParameter("limit", "1234");
        launchJob(batchContext);
    }

    /**
     * Query DSL to Jpa test
     */
    @Test
    public void testQueryDslToJpa() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(DbToDbJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL.name());
        batchContext.setJobParameter("writerType", DbToDbJob.WriterType.JPA.name());
        batchContext.setJobParameter("limit", "123");
        launchJob(batchContext);
    }

    /**
     * Query DSL Projection to Mybatis test
     */
    @Test
    public void testQueryDslProjectionToMybatis() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(DbToDbJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL_PROJECTION.name());
        batchContext.setJobParameter("writerType", DbToDbJob.WriterType.MYBATIS.name());
        batchContext.setJobParameter("limit", "1234");
        launchJob(batchContext);
    }

    /**
     * Query DSL Projection to Mybatis test
     */
    @Test
    public void testQueryDslProjectionToJpa() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(DbToDbJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL_PROJECTION.name());
        batchContext.setJobParameter("writerType", DbToDbJob.WriterType.JPA.name());
        batchContext.setJobParameter("limit", "1234");
        launchJob(batchContext);
    }

    /**
     * Negative test
     */
    @Test
    public void testMybatisToMybatisWithForceToFail() {
        BatchContext batchContext = new BatchContext();
        batchContext.setJobClass(DbToDbJob.class);
        batchContext.setBaseDate("20210101");
        batchContext.setJobParameter("readerType", DbToDbJob.ReaderType.MYBATIS.name());
        batchContext.setJobParameter("writerType", DbToDbJob.WriterType.MYBATIS.name());
        batchContext.setJobParameter("limit", "1234");
        batchContext.setJobParameter("chunkSize", "100");
        batchContext.setJobParameter("forceToFailCount", "567");
        launchJob(batchContext);
    }
}
