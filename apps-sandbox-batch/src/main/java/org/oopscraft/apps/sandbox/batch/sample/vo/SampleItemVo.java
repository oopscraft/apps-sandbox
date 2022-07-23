package org.oopscraft.apps.sandbox.batch.sample.vo;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SampleItemVo {

    private String sampleId;

    private String id;

    private String name;

    private int number;

    private LocalDate localDate;

    private LocalDateTime localDateTime;

}
