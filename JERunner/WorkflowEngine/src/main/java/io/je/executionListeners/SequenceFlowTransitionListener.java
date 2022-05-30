package io.je.executionListeners;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * Listeners used for debugging mainly
 */
public class SequenceFlowTransitionListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        //JELogger.info(SequenceFlowTransitionListener.class, JEMessages.TRANSITIONING_FROM + " " + execution.getCurrentFlowElement());

    }
}
