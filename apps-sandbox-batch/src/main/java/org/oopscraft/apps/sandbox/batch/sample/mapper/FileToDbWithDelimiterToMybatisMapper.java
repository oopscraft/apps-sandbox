package org.oopscraft.apps.sandbox.batch.sample.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.oopscraft.apps.sandbox.batch.sample.vo.SampleBackupVo;

@Mapper
public interface FileToDbWithDelimiterToMybatisMapper {

    public void insertSampleBackup(SampleBackupVo sampleBackupVo);

}
