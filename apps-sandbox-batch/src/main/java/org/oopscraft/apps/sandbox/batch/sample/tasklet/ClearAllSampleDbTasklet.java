package org.oopscraft.apps.sandbox.batch.sample.tasklet;

import lombok.Builder;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.job.AbstractTasklet;
import org.oopscraft.apps.sandbox.core.sample.entity.*;
import org.springframework.batch.item.ExecutionContext;

@Builder
public class ClearAllSampleDbTasklet extends AbstractTasklet {

    @Override
    public void doExecute(BatchContext batchContext, ExecutionContext executionContext) throws Exception {

        jpaQueryFactory.delete(QSampleEntity.sampleEntity).execute();
        commit();

        jpaQueryFactory.delete(QSampleItemEntity.sampleItemEntity).execute();
        commit();

        jpaQueryFactory.delete(QSampleBackupEntity.sampleBackupEntity).execute();
        commit();

        jpaQueryFactory.delete(QSampleItemBackupEntity.sampleItemBackupEntity).execute();
        commit();

        jpaQueryFactory.delete(QSampleErrorEntity.sampleErrorEntity).execute();
        commit();
    }
}
