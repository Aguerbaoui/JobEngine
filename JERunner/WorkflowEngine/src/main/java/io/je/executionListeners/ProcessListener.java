package io.je.executionListeners;

import io.je.processes.ProcessManager;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class ProcessListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        String id =  execution.getProcessDefinitionId();
        id = id.replace(id.substring(id.indexOf(':'), id.length()), "");
        if(execution.getEventName().equalsIgnoreCase("start")) {
            ProcessManager.setRunning(execution.getProcessDefinitionId(), true);
        }
       if(execution.getEventName().equalsIgnoreCase("end")) {
            ProcessManager.setRunning(execution.getProcessDefinitionId(), false);
        }
    }

}
