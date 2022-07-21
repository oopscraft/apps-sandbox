package org.oopscraft.apps.sandbox.batch.sample.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.oopscraft.apps.sandbox.batch.bak.dto.SampleVo;

@Mapper
public interface DbMybatisToDbMybatisMapper {

    public Cursor<SampleVo> selectSamples(@Param("limit")int limit);

}
