package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.db.JpaDbItemWriter;
import org.oopscraft.apps.batch.item.file.DelimiterFileItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
@BatchComponentScan
public class FileToDbWithFixedLengthToJpaJob extends AbstractJob {

    private long size;

    private String filePath;

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
        filePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.fld", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleFileTasklet.builder()
                .size(size)
                .fileType(CreateSampleFileTasklet.FileType.TSV)
                .filePath(filePath)
                .build());

        // 2. 데이터 처리
        addStep(fileToDbStep());
    }

    /**
     * file to db step
     * @return
     */
    public Step fileToDbStep() {
        return stepBuilderFactory.get("fileToDbStep")
                .<SampleVo, SampleBackupEntity>chunk(10)
                .reader(fileItemReader(filePath))
                .processor(convertProcessor())
                .writer(dbItemWriter())
                .build();
    }

    /**
     * file reader
     * @return
     */
    public DelimiterFileItemReader<SampleVo> fileItemReader(String filePath) {
        return createDelimiterFileItemReaderBuilder(SampleVo.class)
                .name("fileItemReader")
                .filePath(filePath)
                .build();
    }

    /**
     * convert processor
     * @return
     */
    public ItemProcessor<SampleVo, SampleBackupEntity> convertProcessor() {
        return item -> {
            SampleBackupEntity sampleBackupEntity = SampleBackupEntity.builder()
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
            return sampleBackupEntity;
        };
    }


    /**
     * db item writer
     * @return
     */
    public JpaDbItemWriter<SampleBackupEntity> dbItemWriter() {
        return createJpaDbItemWriterBuilder(SampleBackupEntity.class)
                .name("dbItemWriter")
                .build();
    }
}
