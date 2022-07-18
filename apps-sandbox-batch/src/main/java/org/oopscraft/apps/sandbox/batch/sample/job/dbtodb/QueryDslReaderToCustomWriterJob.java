package org.oopscraft.apps.sandbox.batch.sample.job.dbtodb;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.item.db.QueryDslDbItemReader;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.oopscraft.apps.sandbox.batch.sample.dto.SampleVo;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.CreateSampleTasklet;
import org.oopscraft.apps.sandbox.batch.sample.tasklet.VerifySampleBackupTasklet;
import org.oopscraft.apps.sandbox.core.sample.dao.SampleBackupRepository;
import org.oopscraft.apps.sandbox.core.sample.dto.QSampleEntity;
import org.springframework.batch.core.Step;
import org.springframework.batch.item.ItemWriter;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class QueryDslReaderToCustomWriterJob extends AbstractJob {

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
        addTasklet(new CreateSampleTasklet(size));

        // 2. 데이터 처리 (Mybatis reader -> Jpa writer)
        addStep(copySampleStep());

        // 3. 결과 검증
        addTasklet(new VerifySampleBackupTasklet());
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
     * Query DSL reader
     * @return
     */
    public QueryDslDbItemReader<SampleVo> queryDslReader() {

        // Query
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

        // creates query dsl reader
        return createQueryDslDbItemReader(SampleVo.class)
                .query(query)
                .build();
    }

    /**
     * Jpa writer
     * @return
     */
    public ItemWriter<SampleVo> customWriter() {
        return items -> {

        };
    }

}
