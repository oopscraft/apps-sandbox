package org.oopscraft.apps.sandbox.batch.sample.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractMapperTest;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;

@Slf4j
@RequiredArgsConstructor
class DbMybatisToDbJpaMapperTest extends AbstractMapperTest {

    private final DbToDbWithMybatisToJpaMapper dbMybatisToDbJpaMapper;
    @Test
    void selectSamples() {
        Cursor<SampleVo> samplesCursor = dbMybatisToDbJpaMapper.selectSamples(1234);
        samplesCursor.forEach(sample->{
           log.debug(sample.toString());
        });
    }
}