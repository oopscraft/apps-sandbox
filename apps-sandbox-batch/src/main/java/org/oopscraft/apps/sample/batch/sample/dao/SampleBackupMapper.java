package org.oopscraft.apps.sample.batch.sample.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.oopscraft.apps.sample.batch.sample.dto.SampleBackupVo;

@Mapper
public interface SampleBackupMapper {

    public Cursor<SampleBackupVo> selectSampleBackups(@Param("limit")int limit);

    public SampleBackupVo selectSampleBackup(@Param("id")String id);

    public Integer insertSampleBackup(SampleBackupVo sampleBackupVo);

    public Integer deleteSampleBackupAll();

    public Integer selectSampleBackupCount();

}
