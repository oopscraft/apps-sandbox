package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.core.sample.entity.*;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleItemRepository;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Builder
public class CreateSampleDbTasklet extends AbstractTasklet {

    private final long size;

    @Autowired
    private SampleRepository sampleRepository;

    @Autowired
    private SampleItemRepository sampleItemRepository;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // 테스트 데이터 입력 처리
        for(int i = 0; i < size; i ++ ) {
            Faker faker = new Faker(new Locale("ko"), new Random(i));
            SampleEntity sampleEntity = SampleEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .name(faker.name().lastName() + faker.name().firstName())
                    .number(faker.number().numberBetween(-100,100))
                    .longNumber(faker.number().numberBetween(-1000_000_000,1000_000_000))
                    .doubleNumber(faker.number().randomDouble(4,-100000,100000))
                    .bigDecimal(BigDecimal.valueOf(faker.number().randomDouble(4,-1234567890, 1234567890)))
                    .sqlDate(new java.sql.Date(System.currentTimeMillis() + faker.number().numberBetween(0,1234)))
                    .utilDate(new java.util.Date(System.currentTimeMillis() + faker.number().numberBetween(0,1234)))
                    .timestamp(new java.sql.Timestamp(System.currentTimeMillis() + faker.number().numberBetween(0,1234)))
                    .localDate(LocalDate.now().plusDays(faker.number().numberBetween(0,123)))
                    .localDateTime(LocalDateTime.now().withNano(0).plusSeconds(faker.number().numberBetween(0,1234)))
                    .lobText(faker.address().fullAddress())
                    .cryptoText(faker.business().creditCardNumber())
                    .build();
            sampleRepository.saveAndFlush(sampleEntity);

            // insert items
            for(int ii = 0; ii < 3; ii ++) {
                SampleItemEntity sampleItemEntity = SampleItemEntity.builder()
                        .sampleId(sampleEntity.getId())
                        .id(UUID.randomUUID().toString())
                        .name(faker.name().lastName() + faker.name().firstName())
                        .number(faker.number().numberBetween(-100,100))
                        .localDate(LocalDate.now().plusDays(faker.number().numberBetween(-123,123)))
                        .localDateTime(LocalDateTime.now().plusDays(faker.number().numberBetween(-123,123)))
                        .build();
                sampleItemRepository.saveAndFlush(sampleItemEntity);
            }

            if(i%10 == 0){
                commit();
            }
        }
        commit();
   }

}
