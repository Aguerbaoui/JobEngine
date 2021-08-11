package io.je.runtime.workflow;

import io.je.JEProcess;
import io.je.processes.ProcessManager;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.WorkflowAlreadyRunningException;
import io.je.utilities.exceptions.WorkflowBuildException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.exceptions.WorkflwTriggeredByEventException;
import io.je.utilities.logger.JELogger;

import java.util.HashMap;
import java.util.ResourceBundle;

/*
 * Workflow Engine handler class
 * */
public class WorkflowEngineHandler {


     private static final  HashMap<String, ProcessManager> processManagerHashMap = new HashMap<>();


    /*
     * Deploy bpmn process
     * */
    public static void deployBPMN(String projectId, String key) throws WorkflowBuildException{
        processManagerHashMap.get(projectId).deployProcess(key);
    }

    /*
     * Register workflow callbacks
     * */
    public static void registerWorkflow(String projectId, String processId) {
        processManagerHashMap.get(projectId).registerWorkflowCallback(processId, new WorkflowCallback());

    }

    /*
     * Launch process without variables
     * */
    public static void launchProcessWithoutVariables(String projectId, String processId) throws WorkflowNotFoundException, WorkflowAlreadyRunningException, WorkflwTriggeredByEventException, WorkflowBuildException {
		JELogger.trace("[projectId = " + projectId +"][workflow = "+processId+"]"+JEMessages.REMOVING_WF);
        processManagerHashMap.get(projectId).launchProcessByKeyWithoutVariables(processId);
    }

    /*
     * Add new process
     * */
    public static void addProcess(JEProcess process) {

        if ( !processManagerHashMap.containsKey(process.getProjectId()))
        {
            processManagerHashMap.put(process.getProjectId(), new ProcessManager());
        }
        processManagerHashMap.get(process.getProjectId()).addProcess(process);
        //registerWorkflow(process.getProjectId(), process.getKey());
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
    }

    /*
     * Trigger event by message
     * */
    public static void throwMessageEventInWorkflow(String projectId, String msg) {
        processManagerHashMap.get(projectId).throwMessageEvent(msg);
    }

    /*
     * Trigger event by message
     * */
    public static void throwSignalEventInWorkflow(String projectId, String msg) {
        processManagerHashMap.get(projectId).throwSignal(msg);
    }

    /*
     * Main test
     * */
    public static void main(String[] args) {
    }

    /*
    * Run all deployed workflows
    * */
    public static void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
       if(processManagerHashMap.containsKey(projectId))
    	{
    	   processManagerHashMap.get(projectId).runAll(projectId);
    	}
    }

    /*
    * Deploy project workflows
    * */
    public static void buildProject(String projectId) throws WorkflowBuildException {
        processManagerHashMap.get(projectId).buildProjectWorkflows(projectId);
    }

    /*
    * Stop project workflows
    * */
    public static void stopProjectWorfklows(String projectId) {
    	if(processManagerHashMap.containsKey(projectId))
    	{
    		JELogger.trace("[projectId = " + projectId +"]"+JEMessages.STOPPING_WORKFLOW);
            processManagerHashMap.get(projectId).stopProjectWorkflows();

    	}
    }

    /*
    * Start workflow by message id
    * */
    public static void startProcessInstanceByMessage(String projectId, String messageEvent) {
    	if(processManagerHashMap.containsKey(projectId))
    	{
            processManagerHashMap.get(projectId).launchProcessByMessageWithoutVariables(messageEvent);

    	}
    }

    public static void deleteProjectProcesses(String projectId) {
		JELogger.trace("[projectId = " + projectId +"]"+JEMessages.REMOVING_WFS);
        if(processManagerHashMap.containsKey(projectId)) {
            stopProjectWorfklows(projectId);
            processManagerHashMap.remove(projectId);
        }


    }

    //remove/stop workflow from runner
    public static void deleteProcess(String projectId, String workflowId) {
        if(processManagerHashMap.containsKey(projectId)) {
            processManagerHashMap.get(projectId).removeProcess(workflowId);
        }
    }
}
