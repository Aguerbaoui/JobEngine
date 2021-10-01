package io.je.serviceTasks;

import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import org.activiti.engine.delegate.DelegateExecution;

public class InformServiceTask extends ServiceTask {
    @Override
    public void execute(DelegateExecution execution) {
        InformTask informTask = (InformTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
        JELogger.info(informTask.getMessage(), LogCategory.RUNTIME, informTask.getProjectId(),
                LogSubModule.WORKFLOW, informTask.getTaskName());

    }
}
