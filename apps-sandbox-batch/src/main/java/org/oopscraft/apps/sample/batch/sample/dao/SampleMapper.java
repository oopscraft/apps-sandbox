package org.oopscraft.apps.sample.batch.sample.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.oopscraft.apps.sample.batch.sample.dto.SampleVo;

@Mapper
public interface SampleMapper {

    public Cursor<SampleVo> selectSamples(@Param("limit")int limit);

    public SampleVo selectSample(@Param("id")String id);

    public Integer insertSample(SampleVo sampleVo);

    public Integer deleteSampleAll();

    public Integer selectSampleCount();

}
