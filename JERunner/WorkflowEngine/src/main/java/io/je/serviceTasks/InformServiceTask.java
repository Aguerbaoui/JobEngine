package io.je.serviceTasks;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.DelegateExecution;

public class InformServiceTask extends ServiceTask{
    @Override
    public void execute(DelegateExecution execution) {
        if(execution.getVariable(execution.getCurrentActivityId()) != null) {
            JELogger.info(JEMessages.INFORM_FROM_USER + " = " + execution.getVariable(execution.getCurrentActivityId()));
        }
    }
}
