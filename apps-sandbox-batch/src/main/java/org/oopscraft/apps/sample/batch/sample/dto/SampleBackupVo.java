package org.oopscraft.apps.sample.batch.sample.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleBackupVo {

    @Length(30)
    private String id;

    @Length(30)
    private String name;

    @Length(30)
    private int number;

    @Length(30)
    private long longNumber;

    @Length(30)
    private double doubleNumber;

    @Length(30)
    private BigDecimal bigDecimal;

    @Length(30)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Date sqlDate;

    @Length(30)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date utilDate;

    @Length(30)
    private java.sql.Timestamp timestamp;

    @Length(30)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;

    @Length(30)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;

    @Length(200)
    private String data;

}
