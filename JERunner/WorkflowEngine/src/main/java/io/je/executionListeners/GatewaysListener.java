package io.je.executionListeners;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class GatewaysListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        String id =  execution.getCurrentActivityId();
        if(execution.getEventName().equalsIgnoreCase("start")) {
            JELogger.info(" " + JEMessages.GATEWAY_ID + " = " + id + " " + JEMessages.JUST_EXECUTED);
        }

    }
}
