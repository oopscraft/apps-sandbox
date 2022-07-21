package org.oopscraft.apps.sandbox.core.sample.repository;

import org.oopscraft.apps.sandbox.core.sample.entity.SampleBackupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleBackupRepository extends JpaRepository<SampleBackupEntity, String>, JpaSpecificationExecutor<SampleBackupEntity> {

}
