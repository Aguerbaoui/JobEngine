package io.je.executionListeners;

import io.je.processes.ProcessManager;
import io.je.utilities.log.JELogger;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class ProcessListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        String id =  execution.getProcessDefinitionId(); //"testWorkflow"
        String processInstanceId = execution.getProcessInstanceId();// "4"
        id = id.replace(id.substring(id.indexOf(':'), id.length()), "");
        if(execution.getEventName().equalsIgnoreCase("start")) {
            //JELogger.debug("Started process in activity engine " + id);
            //ProcessManager.setRunning(execution.getProcessDefinitionId(), true, processInstanceId);
        }
       if(execution.getEventName().equalsIgnoreCase("end")) {
           //JELogger.debug("Done with process in activity engine " + id);
            //ProcessManager.setRunning(execution.getProcessDefinitionId(), false, processInstanceId);
        }
    }

}
