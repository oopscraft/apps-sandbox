package org.oopscraft.apps.module.core.sample.dao;

import org.oopscraft.apps.module.core.sample.dto.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, String>, JpaSpecificationExecutor<SampleEntity>/*, SampleRepositorySupport*/ {

    public SampleEntity findByName(String name);

}
