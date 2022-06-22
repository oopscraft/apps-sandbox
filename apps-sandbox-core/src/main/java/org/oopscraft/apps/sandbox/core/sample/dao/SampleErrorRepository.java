package org.oopscraft.apps.sandbox.core.sample.dao;

import org.oopscraft.apps.sandbox.core.sample.dto.SampleErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleErrorRepository extends JpaRepository<SampleErrorEntity, String>, JpaSpecificationExecutor<SampleErrorEntity> {

}
