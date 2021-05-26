package io.je.executionListeners;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class SequenceFlowTransitionListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        JELogger.info(SequenceFlowTransitionListener.class, JEMessages.TRANSITIONING_FROM + " " + execution.getCurrentFlowElement());

    }
}
