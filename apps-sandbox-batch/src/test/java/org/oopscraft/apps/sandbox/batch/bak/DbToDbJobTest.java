package org.oopscraft.apps.sandbox.batch.bak;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;

@Slf4j
public class DbToDbJobTest extends AbstractJobTestSupport {

//    /**
//     * Mybatis to Mybatis test
//     */
//    @Test
//    public void testMybatisToMybatis() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate("20210101")
//                .jobParameter("readerType", DbToDbJob.ReaderType.MYBATIS.name())
//                .jobParameter("writerType", DbToDbJob.ReaderType.MYBATIS.name())
//                .jobParameter("limit", "123")
//                .build();
//        launchJob(batchContext);
//    }
//
//    /**
//     * Mybatis to Mybatis test
//     */
//    @Test
//    public void testMybatisToJpa() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate(getCurrentBaseDate())
//                .jobParameter("readerType", DbToDbJob.ReaderType.MYBATIS.name())
//                .jobParameter("writerType", DbToDbJob.WriterType.JPA.name())
//                .jobParameter("limit", "123")
//                .build();
//       launchJob(batchContext);
//    }
//
//    /**
//     * Query DSL to Mybatis test
//     */
//    @Test
//    public void testQueryDslToMybatis() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate(getCurrentBaseDate())
//                .jobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL.name())
//                .jobParameter("writerType", DbToDbJob.WriterType.MYBATIS.name())
//                .jobParameter("limit", "1234")
//                .build();
//        launchJob(batchContext);
//    }
//
//    /**
//     * Query DSL to Jpa test
//     */
//    @Test
//    public void testQueryDslToJpa() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate(getCurrentBaseDate())
//                .jobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL.name())
//                .jobParameter("writerType", DbToDbJob.WriterType.JPA.name())
//                .jobParameter("limit", "123")
//                .build();
//        launchJob(batchContext);
//    }
//
//    /**
//     * Query DSL Projection to Mybatis test
//     */
//    @Test
//    public void testQueryDslProjectionToMybatis() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate(getCurrentBaseDate())
//                .jobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL_PROJECTION.name())
//                .jobParameter("writerType", DbToDbJob.WriterType.MYBATIS.name())
//                .jobParameter("limit", "1234")
//                .build();
//        launchJob(batchContext);
//    }
//
//    /**
//     * Query DSL Projection to Mybatis test
//     */
//    @Test
//    public void testQueryDslProjectionToJpa() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate(getCurrentBaseDate())
//                .jobParameter("readerType", DbToDbJob.ReaderType.QUERY_DSL_PROJECTION.name())
//                .jobParameter("writerType", DbToDbJob.WriterType.JPA.name())
//                .jobParameter("limit", "1234")
//                .build();
//        launchJob(batchContext);
//    }
//
//    /**
//     * Negative test
//     */
//    @Test
//    public void testMybatisToMybatisWithForceToFail() {
//        BatchContext batchContext = BatchContext.builder()
//                .jobClass(DbToDbJob.class)
//                .baseDate(getCurrentBaseDate())
//                .jobParameter("readerType", DbToDbJob.ReaderType.MYBATIS.name())
//                .jobParameter("writerType", DbToDbJob.WriterType.MYBATIS.name())
//                .jobParameter("limit", "1234")
//                .jobParameter("chunkSize", "100")
//                .jobParameter("forceToFailCount", "567")
//                .build();
//        launchJob(batchContext);
//    }
}
