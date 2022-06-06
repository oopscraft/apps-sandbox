package org.oopscraft.apps.module.batch.sample.tasklet;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.context.BatchContext;
import org.oopscraft.apps.batch.tasklet.AbstractTasklet;
import org.oopscraft.apps.module.batch.sample.dao.SampleBackupMapper;
import org.oopscraft.apps.module.batch.sample.dao.SampleMapper;
import org.oopscraft.apps.module.batch.sample.dto.SampleVo;
import org.oopscraft.apps.module.core.sample.dao.SampleErrorRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Slf4j
@Builder
public class CreateSampleTasklet extends AbstractTasklet {

    // create size
    private int limit;

    @Autowired
    private SampleMapper sampleMapper;

    @Autowired
    private SampleBackupMapper sampleBackupMapper;

    @Autowired
    private SampleErrorRepository sampleErrorRepository;

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        // checks validation
        Assert.isTrue(limit > 0, "limit must be over 0");

        // deletes all sample data
        sampleMapper.deleteSampleAll();
        sampleBackupMapper.deleteSampleBackupAll();
        sampleErrorRepository.deleteAll();

        // creates sample data
        for(int i = 0; i < limit; i ++ ) {

            // sets value object
            SampleVo sampleVo = SampleVo.builder()
                    .id(String.format("id_%s", i))
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
                    .data(String.format("가나다라동해물과백두산뷁읅읋흟갏궯ㄱㄴㄷㄹㄷㅄㅈ~~~%d",i))
                    .build();
            log.debug("sampleVo:{}", sampleVo);

            // insert data
            sampleMapper.insertSample(sampleVo);

            // commit
            commit();
        }

        // final commit
        commit();
    }

}
