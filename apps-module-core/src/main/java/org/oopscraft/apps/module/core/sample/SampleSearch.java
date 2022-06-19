package org.oopscraft.apps.module.core.sample;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleSearch {

    public static enum DaoType { JPA, QUERY_DSL, MYBATIS }

    private DaoType daoType;

    private String id;

    private String name;

}
