package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleRepository;
import org.oopscraft.apps.sandbox.core.sample.dto.QSampleBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.dto.QSampleEntity;
import org.oopscraft.apps.sandbox.core.sample.repository.SampleEntity;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Slf4j
@RequiredArgsConstructor
public class CreateSampleTasklet extends AbstractTasklet {

    private final long size;

    @Autowired
    private SampleRepository sampleRepository;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // 1. 데이터 삭제
        jpaQueryFactory.delete(QSampleEntity.sampleEntity).execute();
        commit();
        jpaQueryFactory.delete(QSampleBackupEntity.sampleBackupEntity).execute();
        commit();

        // 2. 테스트 데이터 입력 처리
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
                    .localDateTime(LocalDateTime.now())
                    .localDate(LocalDate.now())
                    .lobText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                    .cryptoText(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                    .build();
            sampleRepository.saveAndFlush(sampleEntity);
            if(i%10 == 0){
                commit();
            }
        }
        commit();
   }

}
