package org.oopscraft.apps.sandbox.batch.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReaderConfigurable;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemWriterConfigurable;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateConfigurableSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.*;
import org.springframework.batch.core.Step;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
import java.util.Optional;

@Deprecated
@Slf4j
@RequiredArgsConstructor
@BatchComponentScan
public class FileToFileWithConfigurableToConfigurableJob extends AbstractJob {

    private long size;

    private String inputFilePath;

    private String outputFilePath;

    private long readCountTypeA = 0;

    private long readCountTypeB = 0;

    private long writeCountTypeA = 0;

    private long writeCountTypeB = 0;

    /**
     * initailize
     * @param batchContext
     */
    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));
        inputFilePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.fld", getBatchContext().getBaseDate());
        outputFilePath = BatchConfig.getDataDirectory(this) + String.format("sample_backup_%s.fld", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateConfigurableSampleFileTasklet.builder()
                .size(size)
                .fileType(CreateConfigurableSampleFileTasklet.FileType.FLD)
                .filePath(inputFilePath)
                .build());

        // 2. 데이터 처리
        addStep(fileToFileStep());

        // 3. 처리건수 확인
        addStep(compareCountStep());
    }

    /**
     * file to file step
     * @return
     */
    public Step fileToFileStep() {
        return stepBuilderFactory.get("fileToDbStep")
                .<Object,Object>chunk(10)
                .reader(fileItemReader())
                .writer(fileItemWriter())
                .build();
    }

    /**
     * file item reader
     * @return
     */
    public FixedLengthFileItemReaderConfigurable<Object> fileItemReader() {
        FixedLengthFileItemReaderConfigurable<Object> fileItemReader = new FixedLengthFileItemReaderConfigurable<>() {
            @Override
            public Object internalRead(String line, int lineNumber) {
                ConfigurableVo item = (ConfigurableVo)mapLine(line, ConfigurableVo.class);
                String type = item.getType();

                // type A
                if("A".equals(type)) {
                    readCountTypeA++;
                    return mapLine(line, ConfigurableSampleVo.class);
                }

                // type B
                if ("B".equals(type)) {
                    readCountTypeB++;
                    return mapLine(line, ConfigurableSampleItemVo.class);
                }
                throw new RuntimeException("invalid type");
            }
        };
        fileItemReader.setName("fileItemReader");
        fileItemReader.setItemType(ConfigurableVo.class);
        fileItemReader.setFilePath(inputFilePath);
        return fileItemReader;
    }

    /**
     * file item writer
     * @return
     */
    public FixedLengthFileItemWriterConfigurable<Object> fileItemWriter() {
        FixedLengthFileItemWriterConfigurable<Object> fileWriter = new FixedLengthFileItemWriterConfigurable<>() {
            @Override
            public void internalWrite(List<? extends Object> items) throws Exception {
                for(Object item : items) {

                    // type A
                    if(item instanceof ConfigurableSampleVo) {
                        ConfigurableSampleVo sampleVo = (ConfigurableSampleVo)item;
                        ConfigurableSampleBackupVo sampleBackupVo = ConfigurableSampleBackupVo.builder()
                                .id(sampleVo.getId())
                                .name(sampleVo.getName())
                                .number(sampleVo.getNumber())
                                .longNumber(sampleVo.getLongNumber())
                                .doubleNumber(sampleVo.getDoubleNumber())
                                .bigDecimal(sampleVo.getBigDecimal())
                                .sqlDate(sampleVo.getSqlDate())
                                .utilDate(sampleVo.getUtilDate())
                                .timestamp(sampleVo.getTimestamp())
                                .localDate(sampleVo.getLocalDate())
                                .localDateTime(sampleVo.getLocalDateTime())
                                .lobText(sampleVo.getLobText())
                                .cryptoText(sampleVo.getCryptoText())
                                .build();
                        write(sampleBackupVo);
                        writeCountTypeA ++;
                    }

                    // type B
                    if(item instanceof ConfigurableSampleItemVo) {
                        ConfigurableSampleItemVo sampleItemVo = (ConfigurableSampleItemVo)item;
                        ConfigurableSampleItemBackupVo sampleItemBackupVo = ConfigurableSampleItemBackupVo.builder()
                                .sampleId(sampleItemVo.getSampleId())
                                .id(sampleItemVo.getId())
                                .name(sampleItemVo.getName())
                                .number(sampleItemVo.getNumber())
                                .localDate(sampleItemVo.getLocalDate())
                                .localDateTime(sampleItemVo.getLocalDateTime())
                                .build();
                        write(sampleItemBackupVo);
                        writeCountTypeB ++;
                    }
                }
            }
        };
        fileWriter.setName("fileItemWriter");
        fileWriter.setItemType(SampleVo.class);
        fileWriter.setFilePath(outputFilePath);
        return fileWriter;
    }

    /**
     * compare count step
     * @return
     */
    public Step compareCountStep() {
        return stepBuilderFactory.get("compareCountStep")
                .tasklet((contribution, chunkContext) -> {
//                    log.info("== readCountTypeA:{}, writeCountTypeA:{}, sampleBackupCount:{}", readCountTypeA, writeCountTypeA, sampleBackupCount);
//                    log.info("== readCountTypeB:{}, writeCountTypeB:{}, sampleItemBackupCount:{}", readCountTypeB, writeCountTypeB, sampleItemBackupCount);
//                    Assert.assertEquals(readCountTypeA, writeCountTypeA, sampleBackupCount);
//                    Assert.assertEquals(readCountTypeB, writeCountTypeB, sampleItemBackupCount);
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
