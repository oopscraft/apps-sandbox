package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemReader;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemWriter;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReader;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemWriter;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleBackupVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
@BatchComponentScan
public class FileToFileWithFixedLengthToDelimiterJob extends AbstractJob {

    private long size;

    private String inputFilePath;

    private String outputFilePath;

    /**
     * initialize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // parameter
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // defines
        inputFilePath = BatchConfig.getDataDirectory(this) + String.format("input_sample_%s.fld", getBatchContext().getBaseDate());
        outputFilePath = BatchConfig.getDataDirectory(this) + String.format("output_sample_backup_%s.tsv", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleFileTasklet.builder()
                .size(size)
                .fileType(CreateSampleFileTasklet.FileType.FLD)
                .filePath(inputFilePath)
                .build());

        // 2. 데이터 처리
        addStep(fileToDbStep());
    }

    /**
     * file to db
     * @return
     */
    public Step fileToDbStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo, SampleBackupVo>chunk(10)
                .reader(fileItemReader())
                .processor(convertProcessor())
                .writer(fileItemWriter())
                .build();
    }

    /**
     * file item reader
     * @return
     */
    public FixedLengthFileItemReader<SampleVo> fileItemReader() {
        return createFixedLengthFileItemReaderBuilder(SampleVo.class)
                .name("fileItemReader")
                .filePath(inputFilePath)
                .build();
    }

    /**
     * convert processor
     * @return
     */
    public ItemProcessor<SampleVo, SampleBackupVo> convertProcessor() {
        return item -> {
            SampleBackupVo sampleBackupVo = SampleBackupVo.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .number(item.getNumber())
                    .longNumber(item.getLongNumber())
                    .doubleNumber(item.getDoubleNumber())
                    .bigDecimal(item.getBigDecimal())
                    .sqlDate(item.getSqlDate())
                    .utilDate(item.getUtilDate())
                    .timestamp(item.getTimestamp())
                    .localDate(item.getLocalDate())
                    .localDateTime(item.getLocalDateTime())
                    .lobText(item.getLobText())
                    .cryptoText(item.getCryptoText())
                    .build();
            return sampleBackupVo;
        };
    }

    /**
     * file writer
     *
     * @return
     */
    public DelimiterFileItemWriter<SampleBackupVo> fileItemWriter() {
        return createDelimiterFileItemWriterBuilder(SampleBackupVo.class)
                .name("fileItemWriter")
                .filePath(outputFilePath)
                .build();
    }

}
