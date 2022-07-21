package org.oopscraft.apps.sandbox.core.sample.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sandbox_sample_backup")
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleBackupEntity extends BaseEntity {

    @Id
    @Column(name = "id", length=32)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number")
    private int number;

    @Column(name = "long_number")
    private long longNumber;

    @Column(name = "double_number")
    private double doubleNumber;

    @Column(name = "big_decimal")
    private BigDecimal bigDecimal;

    @Column(name = "sql_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Date sqlDate;

    @Column(name = "util_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date utilDate;

    @Column(name = "timestamp")
    private java.sql.Timestamp timestamp;

    @Column(name = "local_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;

    @Column(name = "local_date_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime localDateTime;

    @Column(name = "lob_text")
    @Lob
    private String lobText;

    @Column(name = "crypto_text")
    @Lob
    private String cryptoText;

}
