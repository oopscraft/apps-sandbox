package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import org.junit.jupiter.api.Test;
import org.oopscraft.apps.batch.BatchConfig;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.batch.test.AbstractJobTest;
import org.springframework.batch.core.step.tasklet.Tasklet;


public class CreateSampleFileTaskletTest extends AbstractJobTest {

    @Test
    public void testCreateTsv() {
        AbstractTasklet tasklet = CreateSampleFileTasklet.builder()
                .size(123)
                .fileType(CreateSampleFileTasklet.FileType.TSV)
                .filePath(BatchConfig.getDataDirectory(this) + "sample_item.tsv")
                .build();
        runTasklet(tasklet);
    }

    @Test
    public void testCreateFld() {
        Tasklet tasklet = CreateSampleFileTasklet.builder()
                .size(123)
                .fileType(CreateSampleFileTasklet.FileType.FLD)
                .filePath(BatchConfig.getDataDirectory(this) + "sample_item.fld")
                .build();
        runTasklet(tasklet);
    }

}
