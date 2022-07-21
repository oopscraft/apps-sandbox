package org.oopscraft.apps.sandbox.core.sample.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface SampleMapper {

    public List<SampleVo> selectSamples(@Param("id") String id, @Param("name") String name, RowBounds rowBounds);

}
