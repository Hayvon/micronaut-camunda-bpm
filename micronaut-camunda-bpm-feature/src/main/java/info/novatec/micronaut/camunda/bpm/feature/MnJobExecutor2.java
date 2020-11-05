package info.novatec.micronaut.camunda.bpm.feature;

import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

public class MnJobExecutor2 extends JobExecutor {
    private TaskExecutor taskExecutor;

    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void executeJobs(List<String> jobIds, ProcessEngineImpl processEngine) {
        try {
            taskExecutor.execute(getExecuteJobsRunnable(jobIds, processEngine));
        } catch (RejectedExecutionException e) {

            logRejectedExecution(processEngine, jobIds.size());
            rejectedJobsHandler.jobsRejected(jobIds, processEngine, this);
        }
    }

    @Override
    protected void startExecutingJobs() {
        startJobAcquisitionThread();
    }

    @Override
    protected void stopExecutingJobs() {
        stopJobAcquisitionThread();
    }
}
