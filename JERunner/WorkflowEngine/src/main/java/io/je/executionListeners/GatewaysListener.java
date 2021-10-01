package io.je.executionListeners;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class GatewaysListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        String id =  execution.getCurrentActivityId();
        if(execution.getEventName().equalsIgnoreCase("start")) {
            JELogger.debug(JEMessages.GATEWAY_ID + " = " + id + " " + JEMessages.JUST_EXECUTED,  LogCategory.RUNTIME,
                    null, LogSubModule.WORKFLOW, id);
        }

    }
}
