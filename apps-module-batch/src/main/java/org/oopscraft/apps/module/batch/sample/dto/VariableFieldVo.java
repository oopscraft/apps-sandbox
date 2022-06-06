package org.oopscraft.apps.module.batch.sample.dto;


import org.oopscraft.apps.batch.item.file.annotation.Length;

public class VariableFieldVo {

    @Length(20)
    private String id;

    @Length(20)
    private String name;

    @Length(100)
    private String detail;

    @Length(10)
    private int age;

}
