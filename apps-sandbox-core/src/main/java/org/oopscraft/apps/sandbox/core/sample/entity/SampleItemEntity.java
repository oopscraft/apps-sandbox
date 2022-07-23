package org.oopscraft.apps.sandbox.core.sample.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.oopscraft.apps.core.data.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sandbox_sample_item")
@IdClass(SampleItemEntity.Pk.class)
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SampleItemEntity extends BaseEntity {

    @Data
    public static class Pk implements Serializable {
        private String sampleId;
        private String id;
    }

    @Id
    @Column(name = "sample_id", length = 64)
    private String sampleId;

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number")
    private int number;

    @Column(name = "local_date")
    private LocalDate localDate;

    @Column(name = "local_date_time")
    private LocalDateTime localDateTime;

}
