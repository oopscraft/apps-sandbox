package org.oopscraft.apps.sandbox.batch.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.item.file.FixedLengthFileItemReaderConfigurable;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateConfigurableSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleFileTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.*;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleItemBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleItemBackupRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;

import java.util.Optional;

@Deprecated
@Slf4j
@RequiredArgsConstructor
@BatchComponentScan
public class FileToDbWithConfigurableToCustomJob extends AbstractJob {

    private long size;

    private final SampleBackupRepository sampleBackupRepository;

    private final SampleItemBackupRepository sampleItemBackupRepository;

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

        String filePath = BatchConfig.getDataDirectory(this) + String.format("sample_%s.fld", getBatchContext().getBaseDate());

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateConfigurableSampleFileTasklet.builder()
                .size(size)
                .fileType(CreateConfigurableSampleFileTasklet.FileType.TSV)
                .filePath(filePath)
                .build());

        // 2. 데이터 처리
        addStep(fileToDbStep(filePath));
    }

    /**
     * 데이터 복사
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
    public FixedLengthFileItemReaderConfigurable<Object> fileItemReader(String filePath) {
        FixedLengthFileItemReaderConfigurable<Object> fileItemReader = new FixedLengthFileItemReaderConfigurable<>() {
            @Override
            public Object internalRead(String line, int lineNumber) {
                ConfigurableVo item = (ConfigurableVo)mapLine(line, ConfigurableVo.class);
                String type = item.getType();
                if("1".equals(type)) {
                    return mapLine(line, ConfigurableSampleVo.class);
                }
                if ("2".equals(type)) {
                    return mapLine(line, ConfigurableSampleItemVo.class);
                }
                throw new RuntimeException("invalid type");
            }
        };
        fileItemReader.setName("fileItemReader");
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
                }

                // type B
                if(item instanceof ConfigurableSampleItemVo) {
                    ConfigurableSampleItemVo configurableSampleItemVo = (ConfigurableSampleItemVo)item;
                    SampleItemBackupEntity sampleItemBackupEntity = SampleItemBackupEntity.builder()
                            .id(configurableSampleItemVo.getId())
                            .name(configurableSampleItemVo.getName())
                            .number(configurableSampleItemVo.getNumber())
                            .localDate(configurableSampleItemVo.getLocalDate())
                            .localDateTime(configurableSampleItemVo.getLocalDateTime())
                            .build();
                    sampleItemBackupRepository.saveAndFlush(sampleItemBackupEntity);
                }
            }
        };
    }

}
