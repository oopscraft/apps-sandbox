package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.db.MybatisDbItemWriter;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.mapper.FileToDbWithFixedLengthToMybatisMapper;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleBackupVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
@BatchComponentScan
public class FileToDbWithFixedLengthToMybatisJob extends AbstractJob {

    private long size;

    private String filePath;

    /**
     * initialize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));
        filePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.fld", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleFileTasklet.builder()
                .size(size)
                .fileType(CreateSampleFileTasklet.FileType.FLD)
                .filePath(filePath)
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
                .writer(dbItemWriter())
                .build();
    }

    /**
     * file item reader
     * @return
     */
    public FixedLengthFileItemReader<SampleVo> fileItemReader() {
        return createFixedLengthFileItemReaderBuilder(SampleVo.class)
                .name("fileItemReader")
                .filePath(filePath)
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
     * db item writer
     * @return
     */
    public MybatisDbItemWriter<SampleBackupVo> dbItemWriter() {
        return createMybatisDbItemWriterBuilder(SampleBackupVo.class)
                .name("dbItemWriter")
                .mapperClass(FileToDbWithFixedLengthToMybatisMapper.class)
                .mapperMethod("insertSampleBackup")
                .build();
    }
}
