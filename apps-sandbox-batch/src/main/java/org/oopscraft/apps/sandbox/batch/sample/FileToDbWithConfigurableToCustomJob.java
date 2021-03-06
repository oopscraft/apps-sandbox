package org.oopscraft.apps.sandbox.batch.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateConfigurableSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.ConfigurableSampleItemVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.ConfigurableSampleVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.ConfigurableVo;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleItemBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleItemBackupRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@BatchComponentScan
public class FileToDbWithConfigurableToCustomJob extends AbstractJob {

    private long size;

    private String filePath;

    private final SampleBackupRepository sampleBackupRepository;

    private final SampleItemBackupRepository sampleItemBackupRepository;


    private long readCountTypeA = 0;

    private long readCountTypeB = 0;

    private long writeCountTypeA = 0;

    private long writeCountTypeB = 0;

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

        // 0. ?????????
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. ????????? ????????? ??????
        addStep(CreateConfigurableSampleFileTasklet.builder()
                .size(size)
                .fileType(CreateConfigurableSampleFileTasklet.FileType.FLD)
                .filePath(filePath)
                .build());

        // 2. ????????? ??????
        addStep(fileToDbStep(filePath));

        // 3. ???????????? ??????
        addStep(compareCountStep());
    }

    /**
     * ????????? ??????
     * @return
     */
    public Step fileToDbStep(String filePath) {
        return stepBuilderFactory.get("fileToDbStep")
                .<Object,Object>chunk(10)
                .reader(fileItemReader(filePath))
                .writer(dbItemWriter())
                .build();
    }

    /**
     * file reader
     * @return
     */
    public FixedLengthFileItemReader<Object> fileItemReader(String filePath) {
        FixedLengthFileItemReader<Object> fileItemReader = new FixedLengthFileItemReader.Configurable<>() {
            @Override
            public Object internalRead(String line, int lineNumber) {
                ConfigurableVo item = (ConfigurableVo) mapLine(line, ConfigurableVo.class);
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
        fileItemReader.setFilePath(filePath);
        return fileItemReader;
    }

    /**
     * custom writer
     * @return
     */
    public ItemWriter<Object> dbItemWriter() {
        return items -> {
            for(Object item : items) {

                // type A
                if(item instanceof ConfigurableSampleVo) {
                    ConfigurableSampleVo configurableSampleVo = (ConfigurableSampleVo)item;
                    SampleBackupEntity sampleBackupEntity = SampleBackupEntity.builder()
                            .id(configurableSampleVo.getId())
                            .name(configurableSampleVo.getName())
                            .number(configurableSampleVo.getNumber())
                            .longNumber(configurableSampleVo.getLongNumber())
                            .doubleNumber(configurableSampleVo.getDoubleNumber())
                            .bigDecimal(configurableSampleVo.getBigDecimal())
                            .sqlDate(configurableSampleVo.getSqlDate())
                            .utilDate(configurableSampleVo.getUtilDate())
                            .timestamp(configurableSampleVo.getTimestamp())
                            .localDate(configurableSampleVo.getLocalDate())
                            .localDateTime(configurableSampleVo.getLocalDateTime())
                            .lobText(configurableSampleVo.getLobText())
                            .cryptoText(configurableSampleVo.getCryptoText())
                            .build();
                    sampleBackupRepository.saveAndFlush(sampleBackupEntity);
                    writeCountTypeA ++;
                }

                // type B
                if(item instanceof ConfigurableSampleItemVo) {
                    ConfigurableSampleItemVo configurableSampleItemVo = (ConfigurableSampleItemVo)item;
                    SampleItemBackupEntity sampleItemBackupEntity = SampleItemBackupEntity.builder()
                            .sampleId(configurableSampleItemVo.getSampleId())
                            .id(configurableSampleItemVo.getId())
                            .name(configurableSampleItemVo.getName())
                            .number(configurableSampleItemVo.getNumber())
                            .localDate(configurableSampleItemVo.getLocalDate())
                            .localDateTime(configurableSampleItemVo.getLocalDateTime())
                            .build();
                    sampleItemBackupRepository.saveAndFlush(sampleItemBackupEntity);
                    writeCountTypeB ++;
                }
            }
        };
    }

    /**
     * compare count step
     * @return
     */
    public Step compareCountStep() {
        return stepBuilderFactory.get("compareCountStep")
                .tasklet((contribution, chunkContext) -> {
                    long sampleBackupCount = sampleBackupRepository.count();
                    long sampleItemBackupCount = sampleItemBackupRepository.count();
                    log.info("== readCountTypeA:{}, writeCountTypeA:{}, sampleBackupCount:{}", readCountTypeA, writeCountTypeA, sampleBackupCount);
                    log.info("== readCountTypeB:{}, writeCountTypeB:{}, sampleItemBackupCount:{}", readCountTypeB, writeCountTypeB, sampleItemBackupCount);
                    Assert.assertEquals(readCountTypeA, writeCountTypeA, sampleBackupCount);
                    Assert.assertEquals(readCountTypeB, writeCountTypeB, sampleItemBackupCount);
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
