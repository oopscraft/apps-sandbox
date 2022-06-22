package org.oopscraft.apps.sandbox.core.sample;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleSearch {

    private String id;

    private String name;

}
