package org.oopscraft.apps.sandbox.core.sample.dao;

import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.sandbox.core.sample.dto.SampleVo;

import java.util.List;

public interface SampleRepositorySupport {

    List<SampleVo> findSamples(String id, String name, PageRequest pageRequest);

}
