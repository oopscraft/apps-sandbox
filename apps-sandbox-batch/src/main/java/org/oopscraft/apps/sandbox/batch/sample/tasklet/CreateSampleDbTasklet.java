package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
            SampleEntity sampleEntity = SampleEntity.builder()
                    .id(String.format("id-%d",i))
                    .name(String.format("홍길동%s", i))
                    .number(i)
                    .longNumber((long)i)
                    .doubleNumber(123.456 + i)
                    .bigDecimal(new BigDecimal(i))
                    .sqlDate(new java.sql.Date(System.currentTimeMillis()))
                    .utilDate(new Date())
                    .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                    .localDate(LocalDate.now())
                    .localDateTime(LocalDateTime.now().withNano(0))
                    .lobText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                    .cryptoText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                    .build();
            sampleRepository.saveAndFlush(sampleEntity);

            // insert items
            for(int ii = 0; ii < 3; ii ++) {
                SampleItemEntity sampleItemEntity = SampleItemEntity.builder()
                        .sampleId(sampleEntity.getId())
                        .id(String.format("%d-%d", i, ii))
                        .name(String.format("James-%d", ii))
                        .number(123 + i)
                        .localDate(LocalDate.now())
                        .localDateTime(LocalDateTime.now())
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
