package org.oopscraft.apps.sandbox.batch.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.QSampleEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

public class FileWriterWithCustomHeaderFooterJob extends AbstractJob {

    private long size;

    private String filePath;

    @Override
    public void initialize(BatchContext batchContext) {

        // parameter
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // defines
        filePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.fld", getBatchContext().getBaseDate());

        // 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 테스트 데이터 생성
        addStep(CreateSampleDbTasklet.builder().size(size).build());

        // FLD 파일 출력
        addStep(writeFileStep());

    }

    public Step writeFileStep() {
        return stepBuilderFactory.get("writeFileStep")
                .<SampleVo,SampleVo>chunk(10)
                .reader(dbItemReader())
                .writer(fileItemWriter())
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
    public FixedLengthFileItemWriter<SampleVo> fileItemWriter() {
        FixedLengthFileItemWriter<SampleVo> fileItemWriter = createFixedLengthFileItemWriterBuilder(SampleVo.class)
                .name("fileItemWriter")
                .filePath(filePath)
                .build();

        // defines custom header
        fileItemWriter.setHeaderCallback(writer -> {
            LineAggregator<SampleVo> lineAggregator = fileItemWriter.createLineAggregator(SampleVo.class);
            SampleVo sampleHeaderVo = SampleVo.builder()
                    .id("ID")
                    .name("NAME")
                    .build();
            String line = lineAggregator.aggregate(sampleHeaderVo);
            writer.write(line);
        });

        // defines custom footer
        fileItemWriter.setFooterCallback(writer -> {
            LineAggregator<SampleVo> lineAggregator = fileItemWriter.createLineAggregator(SampleVo.class);
            SampleVo sampleFooterVo = SampleVo.builder()
                    .id("FOOTER_ID")
                    .name("FOOTER_NAME")
                    .build();
            String line = lineAggregator.aggregate(sampleFooterVo);
            writer.write(line);
        });

        return fileItemWriter;
    }

}
