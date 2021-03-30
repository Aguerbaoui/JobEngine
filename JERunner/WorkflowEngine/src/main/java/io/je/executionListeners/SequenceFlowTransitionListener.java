package io.je.executionListeners;

import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class SequenceFlowTransitionListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        JELogger.info(SequenceFlowTransitionListener.class, "Transition from " + execution.getCurrentFlowElement());

    }
}
