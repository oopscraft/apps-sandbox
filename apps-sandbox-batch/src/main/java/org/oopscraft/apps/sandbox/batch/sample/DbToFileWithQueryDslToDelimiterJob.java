package org.oopscraft.apps.sandbox.batch.sample;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CompareFileToSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleEntity;
import org.springframework.batch.core.Step;

import java.util.Optional;


@Slf4j
@BatchComponentScan
public class DbToFileWithQueryDslToDelimiterJob extends AbstractJob {

    private long size;

    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        String filePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.tsv", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleDbTasklet.builder().size(size).build());

        // 2. 데이터 처리
        addStep(dbToFileStep(filePath));

        // 3. 결과 검증
        addStep(CompareFileToSampleDbTasklet.builder()
                .fileType(CompareFileToSampleDbTasklet.FileType.TSV)
                .filePath(filePath)
                .build());
    }

    /**
e    * 데이터 복사
     * @return
     */
    public Step dbToFileStep(String filePath) {
        return stepBuilderFactory.get("copySample")
                .<SampleVo, SampleVo>chunk(10)
                .reader(dbItemReader())
                .writer(fileItemWriter(filePath))
                .build();
    }

    /**
     * query DSL reader
     * @return
     */
    public QueryDslDbItemReader<SampleVo> dbItemReader() {
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<SampleVo> query = jpaQueryFactory
                .select(Projections.fields(SampleVo.class,
                        qSampleEntity.id,
                        qSampleEntity.name,
                        qSampleEntity.number,
                        qSampleEntity.longNumber,
                        qSampleEntity.doubleNumber,
                        qSampleEntity.bigDecimal,
                        qSampleEntity.sqlDate,
                        qSampleEntity.utilDate,
                        qSampleEntity.timestamp,
                        qSampleEntity.localDate,
                        qSampleEntity.localDateTime,
                        qSampleEntity.lobText,
                        qSampleEntity.cryptoText
                )).from(qSampleEntity)
                .limit(size);
        return createQueryDslDbItemReader(SampleVo.class)
                .name("dbItemReader")
                .query(query)
                .build();
    }

    /**
     * file writer
     *
     * @return
     */
    public DelimiterFileItemWriter<SampleVo> fileItemWriter(String filePath) {
        return createDelimiterFileItemWriterBuilder(SampleVo.class)
                .name("fileItemWriter")
                .filePath(filePath)
                .build();
    }

}

