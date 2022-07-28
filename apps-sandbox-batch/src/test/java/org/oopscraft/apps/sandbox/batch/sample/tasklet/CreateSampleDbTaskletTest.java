package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.oopscraft.apps.batch.test.AbstractJobTestSupport;

import static org.junit.jupiter.api.Assertions.*;

class CreateSampleDbTaskletTest extends AbstractJobTest {

    @Test
    public void testDefault() {
        AbstractTasklet tasklet = CreateSampleDbTasklet.builder()
                .size(123)
                .build();
        runTasklet(tasklet);
    }

}