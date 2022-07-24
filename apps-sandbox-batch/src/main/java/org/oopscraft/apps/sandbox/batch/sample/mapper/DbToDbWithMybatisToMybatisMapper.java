package org.oopscraft.apps.sandbox.batch.sample.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleBackupVo;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleVo;

@Mapper
public interface DbToDbWithMybatisToMybatisMapper {

    public Cursor<SampleVo> selectSamples(@Param("limit")int limit);

    public void insertSampleBackup(SampleBackupVo sampleBackupVo);

}
