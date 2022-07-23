package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.db.JpaDbItemWriter;
import org.oopscraft.apps.batch.item.db.MybatisDbItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.mapper.DbMybatisToDbJpaMapper;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CompareSampleDbToBackupDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleDbTasklet;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemProcessor;

import java.util.Optional;

@Slf4j
public class DbMybatisToDbJpaJob extends AbstractJob {

    private long size;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // 1. 테스트 데이터 생성
        addStep(new CreateSampleDbTasklet(size));

        // 2. 데이터 처리 (Mybatis reader -> Jpa writer)
        addStep(copySampleToBackupStep());

        // 3. 결과 검증
        addStep(new CompareSampleDbToBackupDbTasklet());
    }

    /**
     * 데이터 복사
     * @return
     */
    public Step copySampleToBackupStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo, SampleBackupEntity>chunk(10)
                .reader(mybatisReader())
                .processor(convertProcessor())
                .writer(jpaWriter())
                .build();
    }

    /**
     * Mybatis reader
     * @return
     */
    public MybatisDbItemReader<SampleVo> mybatisReader() {
        return createMybatisDbItemReaderBuilder(SampleVo.class)
                .name("mybatisReader")
                .mapperClass(DbMybatisToDbJpaMapper.class)
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
                    .build();
            modelMapper.map(item, sampleBackupEntity);
            return sampleBackupEntity;
        };
    }

    /**
     * Jpa writer
     * @return
     */
    public JpaDbItemWriter<SampleBackupEntity> jpaWriter() {
        return createJpaDbItemWriterBuilder(SampleBackupEntity.class)
                .name("jpaWriter")
                .build();
    }

}
