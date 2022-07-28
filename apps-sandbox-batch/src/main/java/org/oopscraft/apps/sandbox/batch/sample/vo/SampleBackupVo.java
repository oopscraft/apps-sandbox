package org.oopscraft.apps.sandbox.batch.sample.vo;

import lombok.*;
import org.oopscraft.apps.batch.item.file.annotation.Align;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SampleBackupVo {

    @Length(30)
    private String id;

    @Length(100)
    private String name;

    @Length(value = 10, align = Align.RIGHT)
    private int number;

    @Length(value=20, align = Align.RIGHT)
    private long longNumber;

    @Length(value = 20, align = Align.RIGHT)
    private double doubleNumber;

    @Length(value = 20, align = Align.RIGHT)
    private BigDecimal bigDecimal;

    @Length(30)
    private java.sql.Date sqlDate;

    @Length(30)
    private java.util.Date utilDate;

    @Length(30)
    private java.sql.Timestamp timestamp;

    @Length(30)
    private LocalDate localDate;

    @Length(30)
    private LocalDateTime localDateTime;

    @Length(100)
    private String lobText;

    @Length(100)
    private String cryptoText;

}
