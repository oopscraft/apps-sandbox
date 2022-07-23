package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.db.MybatisDbItemReader;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.mapper.DbMybatisToDbJpaMapper;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CompareFileToSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.springframework.batch.core.Step;

import java.util.Optional;

@Slf4j
@BatchComponentScan
public class DbMybatisToFileDelimiterJob extends AbstractJob {

    private long size;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        String filePath = BatchConfig.getDataDirectory(this) + String.format("sample_backup_%s.tsv", getBatchContext().getBaseDate());

        // 1. 테스트 데이터 생성
        addStep(new CreateSampleDbTasklet(size));

        // 2. 데이터 처리
        addStep(copySampleDbToFileStep(filePath));

        // 3. 결과 검증
        addStep(CompareFileToSampleDbTasklet.builder()
                .fileType(CompareFileToSampleDbTasklet.FileType.TSV)
                .filePath(filePath)
                .build());
    }

    /**
     * 데이터 복사
     * @return
     */
    public Step copySampleDbToFileStep(String filePath) {
        return stepBuilderFactory.get("copySample")
                .<SampleVo, SampleVo>chunk(10)
                .reader(dbItemWriter())
                .writer(fileItemWriter(filePath))
                .build();
    }

    /**
     * Mybatis reader
     * @return
     */
    public MybatisDbItemReader<SampleVo> dbItemWriter() {
        return createMybatisDbItemReaderBuilder(SampleVo.class)
                .mapperClass(DbMybatisToDbJpaMapper.class)
                .mapperMethod("selectSamples")
                .parameter("limit", size)
                .build();
    }

    /**
     * file writer
     *
     * @return
     */
    public DelimiterFileItemWriter<SampleVo> fileItemWriter(String filePath) {
       return createDelimiterFileItemWriterBuilder(SampleVo.class)
                .filePath(filePath)
                .build();
    }

}