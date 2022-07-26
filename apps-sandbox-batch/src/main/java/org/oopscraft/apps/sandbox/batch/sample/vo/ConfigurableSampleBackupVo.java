package org.oopscraft.apps.sandbox.batch.sample.vo;

import lombok.*;
import org.oopscraft.apps.batch.item.file.annotation.Align;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfigurableSampleBackupVo {

    @Length(1)
    private String type;

    @Length(32)
    private String id;

    @Length(50)
    private String name;

    @Length(value = 10, align = Align.RIGHT)
    private int number;

    @Length(value = 10, align = Align.RIGHT)
    private long longNumber;

    @Length(value = 10, align = Align.RIGHT)
    private double doubleNumber;

    @Length(value = 10, align = Align.RIGHT)
    private BigDecimal bigDecimal;

    @Length(32)
    private java.sql.Date sqlDate;

    @Length(32)
    private java.util.Date utilDate;

    @Length(32)
    private java.sql.Timestamp timestamp;

    @Length(32)
    private LocalDate localDate;

    @Length(32)
    private LocalDateTime localDateTime;

    @Length(1000)
    private String lobText;

    @Length(1000)
    private String cryptoText;

}
