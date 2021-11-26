package io.je.executionListeners;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import io.je.processes.ProcessManager;

public class ProcessListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        String id =  execution.getProcessDefinitionId(); //"testWorkflow"
        String processInstanceId = execution.getProcessInstanceId();// "4"
        id = id.replace(id.substring(id.indexOf(':'), id.length()), "");
        if(execution.getEventName().equalsIgnoreCase("start")) {
            ProcessManager.setRunning(id, true, processInstanceId);
            //JELogger.debug("Started process in activity engine " + id);
            //ProcessManager.setRunning(execution.getProcessDefinitionId(), true, processInstanceId);
        }
       if(execution.getEventName().equalsIgnoreCase("end")) {
           //JELogger.debug("Done with process in activity engine " + id);
            ProcessManager.setRunning(id, false, processInstanceId);
        }
    }

}
