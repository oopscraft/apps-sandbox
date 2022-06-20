package org.oopscraft.apps.module.web.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.module.core.sample.Sample;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleResponse {

    @Schema(description = "ID" , example = "ABC")
    private String id;

    @Schema(description = "Name" , example = "Apple")
    private String name;

    @Schema(description = "Int number type" , example = "123")
    private int number;

    @Schema(description = "Long number type" , example = "1232322")
    private long longNumber;

    @Schema(description = "Double number type" , example = "123.23")
    private double doubleNumber;

    @Schema(description = "Double number type" , example = "12345.2312322")
    private BigDecimal bigDecimal;

    @Schema(description = "Sql date type" , example = "2021-01-16T16:17:58.555Z")
    private java.sql.Date sqlDate;

    @Schema(description = "Util date type" , example = "2021-01-16T16:17:58.555Z")
    private java.util.Date utilDate;

    @Schema(description = "Timestamp type" , example = "2021-01-16T16:17:58.555Z")
    private java.sql.Timestamp timestamp;

    @Schema(description = "Lob type" , example = "ABCDEFG12345!@#$")
    private String data;

    @Schema(description = "Join column" , example = "ABC")
    private String backupId;

    @Schema(description = "Join column" , example = "Apple")
    private String backupName;


    /**
     * from
     * @param sample
     * @return
     */
    public static SampleResponse from(Sample sample) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(sample, SampleResponse.class);
    }

}
