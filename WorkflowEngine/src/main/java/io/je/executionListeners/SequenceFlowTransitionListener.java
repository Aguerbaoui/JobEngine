package io.je.executionListeners;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import io.je.utilities.logger.JELogger;

public class SequenceFlowTransitionListener implements ExecutionListener{

	public void notify(DelegateExecution execution) {
		JELogger.info("Transition from " + execution.getCurrentFlowElement());
		
	}
}
