package org.oopscraft.apps.sandbox.core.sample.repository;

import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.sandbox.core.sample.mapper.SampleVo;

import java.util.List;

public interface SampleRepositorySupport {

    List<SampleVo> findSamples(String id, String name, PageRequest pageRequest);

}
