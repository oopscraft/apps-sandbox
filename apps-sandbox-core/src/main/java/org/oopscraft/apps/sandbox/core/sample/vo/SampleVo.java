package org.oopscraft.apps.sandbox.core.sample.vo;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SampleVo {

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

    private String lobText;

}
