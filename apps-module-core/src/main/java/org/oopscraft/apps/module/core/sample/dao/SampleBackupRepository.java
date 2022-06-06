package org.oopscraft.apps.module.core.sample.dao;

import org.oopscraft.apps.module.core.sample.dto.SampleBackupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleBackupRepository extends JpaRepository<SampleBackupEntity, String>, JpaSpecificationExecutor<SampleBackupEntity> {

}
