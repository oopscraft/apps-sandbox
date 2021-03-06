package org.oopscraft.apps.sandbox.core.sample.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "apps_sandbox_sample")
@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SampleEntity extends BaseEntity {
	
    @Id
    @Column(name = "id", length=64)
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
    private java.sql.Date sqlDate;

    @Column(name = "util_date")
    private java.util.Date utilDate;

    @Column(name = "timestamp")
    private java.sql.Timestamp timestamp;

    @Column(name = "local_date")
    private LocalDate localDate;

    @Column(name = "local_date_time")
    private LocalDateTime localDateTime;

    @Column(name = "lob_text")
    @Lob
    private String lobText;

    @Column(name = "crypto_text")
    @Lob
    private String cryptoText;

}
