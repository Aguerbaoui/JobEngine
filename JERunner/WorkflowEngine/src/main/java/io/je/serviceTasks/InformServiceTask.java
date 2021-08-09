package io.je.serviceTasks;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.*;
import org.activiti.engine.delegate.DelegateExecution;

import java.time.LocalDateTime;
import java.util.Arrays;

public class InformServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        if(execution.getVariable(execution.getCurrentActivityId()) != null) {
            InformTask informTask = (InformTask) ActivitiTaskManager.getTask(execution.getCurrentActivityId());
            JELogger.info( informTask.getMessage(),  LogCategory.RUNTIME,  informTask.getProjectId(),
                    LogSubModule.WORKFLOW, informTask.getTaskName());
        }
    }
}
