package io.je.executionListeners;

import io.je.processes.ProcessManager;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

/**
 * Listener to monitor the status of the workflow when it starts/finishes
 */
public class ProcessListener implements ExecutionListener {

    public void notify(DelegateExecution execution) {
        String id = execution.getProcessDefinitionId(); //"testWorkflow"
        String processInstanceId = execution.getProcessInstanceId();// "4"
        id = id.replace(id.substring(id.indexOf(':'), id.length()), "");
        if (execution.getEventName()
                .equalsIgnoreCase("start")) {
            ProcessManager.setRunning(id, true, processInstanceId);
            //JELogger.debug("Started process in activity engine " + id);
            //ProcessManager.setRunning(execution.getProcessDefinitionId(), true, processInstanceId);
        }
        if (execution.getEventName()
                .equalsIgnoreCase("end")) {
            //JELogger.debug("Done with process in activity engine " + id);
            ProcessManager.setRunning(id, false, processInstanceId);
        }
    }

}
