package io.je.executionListeners;

import io.je.processes.ProcessManager;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class ProcessListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        JELogger.info(ProcessListener.class, "Process " + execution.getEventName());
       /* if(execution.getEventName().equalsIgnoreCase("end")) {
            ProcessManager.setRunning(execution.getProcessDefinitionId(), false);
        }*/
    }

}
