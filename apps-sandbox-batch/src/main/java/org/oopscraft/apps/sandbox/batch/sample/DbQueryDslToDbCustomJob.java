package org.oopscraft.apps.sandbox.batch.sample;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.dto.SampleVo;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CompareSampleToBackupTasklet;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.dto.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleBackupEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;

import java.util.Optional;

/**
 * DB to DB (QueryDSL Reader to Custom Writer)
 */
@Slf4j
@RequiredArgsConstructor
public class DbQueryDslToDbCustomJob extends AbstractJob {

    private long size;

    private final SampleBackupRepository sampleBackupRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public void initialize(BatchContext batchContext) {

        // 0. 패라미터 체크
        size = Optional.ofNullable(batchContext.getJobParameter("size"))
                .map(value->Long.parseLong(value))
                .orElseThrow(()->new RuntimeException("invalid size"));

        // 1. 테스트 데이터 생성
        addStep(new CreateSampleTasklet(size));

        // 2. 데이터 처리 (Query DSL reader -> custom writer)
        addStep(copySampleStep());

        // 3. 결과 검증
        addStep(new CompareSampleToBackupTasklet());
    }

    /**
     * 데이터 복사
     * @return
     */
    public Step copySampleStep() {
        return stepBuilderFactory.get("copySample")
                .<SampleVo,SampleVo>chunk(10)
                .reader(queryDslReader())
                .writer(customWriter())
                .build();
    }

    /**
     * query DSL reader
     * @return
     */
    public QueryDslDbItemReader<SampleVo> queryDslReader() {
        QSampleEntity qSampleEntity = QSampleEntity.sampleEntity;
        JPAQuery<SampleVo> query = new JPAQuery<>();
        query.select(Projections.fields(SampleVo.class,
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
                .query(query)
                .build();
    }

    /**
     * custom writer
     * @return
     */
    public ItemWriter<SampleVo> customWriter() {
        return items -> {
            for(SampleVo item : items) {
                SampleBackupEntity one = sampleBackupRepository.findById(item.getId()).orElse(null);
                if(one == null){
                    one = SampleBackupEntity.builder()
                            .id(item.getId())
                            .build();
                }
                modelMapper.map(item, one);
                sampleBackupRepository.saveAndFlush(one);

                // copy child items
            }
        };
    }

}
