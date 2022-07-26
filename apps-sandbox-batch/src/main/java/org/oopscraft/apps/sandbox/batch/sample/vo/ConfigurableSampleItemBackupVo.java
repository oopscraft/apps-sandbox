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

    @Length(32)
    private String sampleId;

    @Length(32)
    private String id;

    @Length(50)
    private String name;

    @Length(value = 10, align = Align.RIGHT)
    private int number;

    @Length(32)
    private LocalDate localDate;

    @Length(32)
    private LocalDateTime localDateTime;

}
