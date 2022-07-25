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

    @Length(1)
    private String type;

    @Length(100)
    private String sampleId;

    @Length(100)
    private String id;

    @Length(100)
    private String name;

    @Length(value = 100, align = Align.RIGHT)
    private int number;

    @Length(100)
    private LocalDate localDate;

    @Length(100)
    private LocalDateTime localDateTime;

}
