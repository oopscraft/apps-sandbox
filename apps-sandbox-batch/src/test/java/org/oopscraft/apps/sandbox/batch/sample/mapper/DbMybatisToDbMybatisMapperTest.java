package org.oopscraft.apps.sandbox.batch.sample.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractMapperTest;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleBackupVo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@RequiredArgsConstructor
class DbMybatisToDbMybatisMapperTest extends AbstractMapperTest {

    private final DbMybatisToDbMybatisMapper dbMybatisToDbMybatisMapper;

    @Test
    public void insertSampleBackup() {
        SampleBackupVo sampleBackupVo = SampleBackupVo.builder()
                .id("test")
                .name("test")
                .number(123)
                .doubleNumber(1234.12)
                .longNumber(12345)
                .sqlDate(new java.sql.Date(System.currentTimeMillis()))
                .utilDate(new java.util.Date())
                .localDate(LocalDate.now())
                .localDateTime(LocalDateTime.now())
                .build();
        dbMybatisToDbMybatisMapper.insertSampleBackup(sampleBackupVo);
    }

}