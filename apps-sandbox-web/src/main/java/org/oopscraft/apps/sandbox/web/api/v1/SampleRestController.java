package org.oopscraft.apps.sandbox.web.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.core.data.PageableAsQueryParam;
import org.oopscraft.apps.module.core.sample.Sample;
import org.oopscraft.apps.module.core.sample.SampleSearch;
import org.oopscraft.apps.module.core.sample.SampleService;
import org.oopscraft.apps.sandbox.web.api.v1.dto.SampleResponse;
import org.oopscraft.apps.sandbox.web.api.v1.dto.SaveSampleRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SampleRestController
 * User: chomookun
 * Date: 2021-10-09
 */

@RestController
@RequestMapping("api/v1/sandbox/sample")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "/sandbox/sample", description = "Sandbox Sample")
public class SampleRestController {

    private final SampleService sampleService;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * getSamples
     * @param id
     * @param name
     * @param daoType
     * @param pageRequest
     * @param response
     * @return
     */
    @GetMapping
    @Operation(summary = "Searches list of Sample", description = "Retrieves list of sample with pagination(via JPA,QueryDsl,Mybatis)")
    @Parameter(name = "id", description = "ID", schema = @Schema(type = "string", defaultValue = ""))
    @Parameter(name = "name", description = "Name", schema = @Schema(type = "string", defaultValue = ""))
    @Parameter(name = "_daoType", description = "DAO type", schema = @Schema(type = "string", allowableValues = {"JPA", "QUERY_DSL", "MYBATIS"}, defaultValue = "JPA"))
    @Parameter(name = "pageRequest", hidden = true)
    @PageableAsQueryParam
    public List<SampleResponse> getSamples(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "_daoType", required = false) String daoType,
            PageRequest pageRequest,
            HttpServletResponse response
    ) {
        SampleSearch sampleSearch = SampleSearch.builder()
                .id(id)
                .name(name)
                .build();
        List<SampleResponse> sampleResponses = sampleService.getSamples(sampleSearch, SampleService.DaoType.valueOf(daoType), pageRequest).stream()
                .map(SampleResponse::from)
                .collect(Collectors.toList());
        return sampleResponses;
    }

    /**
     * getSample
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @Operation(summary = "Gets Sample", description = "Returns sample data specified ID")
    @Parameter(name = "id", description = "ID", schema = @Schema(type = "string", defaultValue = ""))
    public SampleResponse getSample(@PathVariable("id") String id) {
        Sample sample = sampleService.getSample(id);
        if(sample != null){
            return new ModelMapper().map(sample, SampleResponse.class);
        }
        return null;
    }

    /**
     * saveSample
     * @param saveSampleRequest
     */
    @PostMapping
    @Operation(summary = "Saves Sample", description = "Saves Sample data into database")
    @Transactional
    public void saveSample(
            @RequestBody SaveSampleRequest saveSampleRequest,
            @RequestParam(value = "_forceException", required = false, defaultValue = "false")Boolean forceException,
            @RequestParam(value = "_ignoreException", required = false, defaultValue = "false")Boolean ignoreException
    ) {
        Sample sample = modelMapper.map(saveSampleRequest, Sample.class);
        sampleService.saveSample(sample, forceException, ignoreException);
    }

    /**
     * deleteSample
     * @param id
     */
    @DeleteMapping("{id}")
    @Operation(summary = "Deletes Sample", description = "Deletes sample data specified ID")
    public void deleteSample(@PathVariable("id") String id) {
        sampleService.deleteSample(id);
    }

}

