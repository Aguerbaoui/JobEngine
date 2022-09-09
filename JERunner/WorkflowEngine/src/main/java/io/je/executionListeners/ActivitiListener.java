package io.je.executionListeners;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import utils.log.LoggerUtils;

public class ActivitiListener implements ActivitiEventListener {


    //https://programming.vip/docs/activiti7-event-listening.html
    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {

            case JOB_EXECUTION_SUCCESS:
                LoggerUtils.trace("TASK completed " + event.getProcessDefinitionId());
                break;

            case JOB_EXECUTION_FAILURE:
                LoggerUtils.trace("TASK has failed...");
                break;
            case PROCESS_COMPLETED:
                LoggerUtils.trace("Process finished" + event.getProcessDefinitionId());
                break;
            default:
                LoggerUtils.trace("Event received: " + event.getType());
        }
    }

    @Override
    public boolean isFailOnException() {
        // The logic in the onEvent method of this listener is not critical, exceptions
        // can be ignored if logging fails...
        return false;
    }
}