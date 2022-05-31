package org.oopscraft.apps.module.web.api.v1;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.code.Code;
import org.oopscraft.apps.core.code.CodeService;
import org.oopscraft.apps.module.web.api.v1.dto.SampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/module/sample")
@RequiredArgsConstructor
public class SampleRestController {


    @GetMapping
    public List<SampleResponse> getCodes(){
        return new ArrayList<SampleResponse>();
    }


}
