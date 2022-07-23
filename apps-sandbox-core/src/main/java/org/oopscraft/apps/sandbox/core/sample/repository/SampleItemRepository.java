package org.oopscraft.apps.sandbox.core.sample.repository;

import org.oopscraft.apps.sandbox.core.sample.entity.SampleEntity;
import org.oopscraft.apps.sandbox.core.sample.entity.SampleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleItemRepository extends JpaRepository<SampleItemEntity, SampleItemEntity.Pk>, JpaSpecificationExecutor<SampleItemEntity> {


}

