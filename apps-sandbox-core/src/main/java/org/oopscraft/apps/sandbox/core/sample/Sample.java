package org.oopscraft.apps.sandbox.core.sample;

import lombok.*;
import org.modelmapper.ModelMapper;
import org.oopscraft.apps.sandbox.core.sample.dto.SampleEntity;
import org.oopscraft.apps.sandbox.core.sample.dto.SampleVo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sample {
	
    private String id;

    private String name;

    private int number;

    private long longNumber;

    private double doubleNumber;

    private BigDecimal bigDecimal;

    private java.sql.Date sqlDate;

    private java.util.Date utilDate;

    private java.sql.Timestamp timestamp;

    private LocalDateTime localDateTime;

    private LocalDate localDate;

    private String data;

    private String backupId;

    private String backupName;

    /**
     * from
     * @param sampleEntity sample entity
     * @return sample
     */
    public static Sample from(SampleEntity sampleEntity) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(sampleEntity, Sample.class);
    }

    /**
     * from
     * @param sampleVo sample value object
     * @return return sample
     */
    public static Sample from(SampleVo sampleVo) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(sampleVo, Sample.class);
    }

}
