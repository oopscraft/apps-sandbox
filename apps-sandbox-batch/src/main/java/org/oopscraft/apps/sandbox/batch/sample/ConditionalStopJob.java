package org.oopscraft.apps.sandbox.batch.sample;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchContext;
import org.oopscraft.apps.batch.dependency.BatchComponentScan;
import org.oopscraft.apps.batch.job.AbstractJob;
import org.springframework.batch.core.Step;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
@BatchComponentScan
public class ConditionalStopJob extends AbstractJob {

    @Override
    public void initialize(BatchContext batchContext) {

        // check step
        addStep(checkStep());

        // process step
        addStep(forceToErrorStep());

    }

    /**
     * checkStep
     * @return
     */
    private Step checkStep() {
        return stepBuilderFactory.get("checkStep")
                .tasklet((contribution, chunkContext) -> {

                    // force to be terminated
                    if(1 == 1) {
                        contribution.getStepExecution().setTerminateOnly();
                    }

                    // finish
                    return RepeatStatus.FINISHED;
                }).build();
    }

    /**
     * forceToError
     * @return
     */
    private Step forceToErrorStep() {
        return stepBuilderFactory.get("forceErrorStep")
                .tasklet((contribution, chunkContext) -> {
                    // force to error
                    int error = 1/0;
                    return RepeatStatus.FINISHED;
                }).build();
    }


}
