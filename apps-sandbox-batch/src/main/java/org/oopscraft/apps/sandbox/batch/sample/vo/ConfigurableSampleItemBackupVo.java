package org.oopscraft.apps.sandbox.batch.sample.vo;

import lombok.*;
import org.oopscraft.apps.batch.item.file.annotation.Align;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfigurableSampleItemBackupVo {

    @Length(10)
    private String type;

    @Length(40)
    private String sampleId;

    @Length(40)
    private String id;

    @Length(20)
    private String name;

    @Length(value = 10, align = Align.RIGHT)
    private int number;

    @Length(30)
    private LocalDate localDate;

    @Length(30)
    private LocalDateTime localDateTime;

}
