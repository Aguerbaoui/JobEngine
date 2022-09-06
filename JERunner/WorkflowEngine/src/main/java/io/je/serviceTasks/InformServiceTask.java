package io.je.serviceTasks;

import io.je.utilities.log.JELogger;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class InformServiceTask extends ServiceTask {
    @Override
    public void execute(DelegateExecution execution) {

        try {
            InformTask informTask = (InformTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
            JELogger.info(informTask.getMessage(), LogCategory.RUNTIME, informTask.getProjectId(),
                    LogSubModule.WORKFLOW, informTask.getWorkflowId(), informTask.getTaskName());
        } catch (Exception e) {
            throw new BpmnError("Error");
        }


    }
}
