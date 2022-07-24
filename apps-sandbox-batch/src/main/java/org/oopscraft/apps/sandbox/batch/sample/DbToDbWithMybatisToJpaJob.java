package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.db.JpaDbItemWriter;
import org.oopscraft.apps.batch.item.db.MybatisDbItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.mapper.DbToDbWithMybatisToJpaMapper;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.ClearAllSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CompareSampleDbToBackupDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
public class DbToDbWithMybatisToJpaJob extends AbstractJob {

    private long size;

    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // 0. 초기화
        addStep(ClearAllSampleDbTasklet.builder().build());

        // 1. 테스트 데이터 생성
        addStep(CreateSampleDbTasklet.builder().size(size).build());

        // 2. db to db
        addStep(dbToDbStep());

        // 3. 결과 검증
        addStep(CompareSampleDbToBackupDbTasklet.builder().build());
    }

    /**
     * 데이터 복사
     * @return
     */
    public Step dbToDbStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo, SampleBackupEntity>chunk(10)
                .reader(dbItemReader())
                .processor(convertProcessor())
                .writer(dbItemWriter())
                .build();
    }

    /**
     * db item reader
     * @return
     */
    public MybatisDbItemReader<SampleVo> dbItemReader() {
        return createMybatisDbItemReaderBuilder(SampleVo.class)
                .name("dbItemReader")
                .mapperClass(DbToDbWithMybatisToJpaMapper.class)
                .mapperMethod("selectSamples")
                .parameter("limit", size)
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
