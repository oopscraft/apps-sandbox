package org.oopscraft.apps.sandbox.core.sample.repository;

import org.oopscraft.apps.sandbox.core.sample.entity.SampleItemBackupEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleItemBackupRepository extends JpaRepository<SampleItemBackupEntity, SampleItemBackupEntity.Pk>, JpaSpecificationExecutor<SampleItemBackupEntity> {


}

