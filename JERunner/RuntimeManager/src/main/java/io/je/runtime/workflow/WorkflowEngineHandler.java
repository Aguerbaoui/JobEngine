package io.je.runtime.workflow;

import io.je.JEProcess;
import io.je.processes.ProcessManager;
import io.je.utilities.exceptions.WorkflowAlreadyRunningException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;

import java.util.HashMap;

/*
 * Workflow Engine handler class
 * */
public class WorkflowEngineHandler {


    private static HashMap<String, ProcessManager> processManagerHashMap = new HashMap<>();


    /*
     * Deploy bpmn process
     * */
    public static void deployBPMN(String projectId, String key) {
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
    public static void launchProcessWithoutVariables(String projectId, String processId) throws WorkflowNotFoundException, WorkflowAlreadyRunningException {
        JELogger.info(WorkflowEngineHandler.class, "running workflow " + processId);
        processManagerHashMap.get(projectId).launchProcessByKeyWithoutVariables(processId);
    }

    /*
     * Add new process
     * */
    public static void addProcess(String processId, String name, String processPath, String projectId, boolean isTriggeredByEvent) {

        if ( !processManagerHashMap.containsKey(projectId))
        {
            processManagerHashMap.put(projectId, new ProcessManager());
        }
        processManagerHashMap.get(projectId).addProcess(new JEProcess(processId, name, processPath, projectId, isTriggeredByEvent));
        registerWorkflow(projectId, processId);
        //deployBPMN(processPath);
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
		/*JELogger.info("running workflow ");
		addProcess("testGenerated", "processes/testGenerated.bpmn");
		try {
			launchProcessWithoutVariables("testGenerated");
		} catch (WorkflowNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

    }

    /*
    * Run all existing workflows
    * */
   /* public static void runAllWorkflows() throws WorkflowNotFoundException {
        ProcessManager.runAll();
    }*/

    /*
    * Run all deployed workflows
    * */
    public static void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
        processManagerHashMap.get(projectId).runAll(projectId);
    }

    /*
    * Deploy project workflows
    * */
    public static void buildProject(String projectId) {
        processManagerHashMap.get(projectId).buildProjectWorkflows(projectId);
    }

    /*
    * Stop workflow by id
    * */
    public static void stopWorkflow(String projectId, String key) {
        processManagerHashMap.get(projectId).stopProcess(key);
    }

    /*
    * Stop project workflows
    * */
    public static void stopProjectWorfklows(String projectId) {
        JELogger.info(WorkflowEngineHandler.class, "Stopping workflow executions for project id = " + projectId);
        processManagerHashMap.get(projectId).stopProjectWorkflows(projectId);
    }

    /*
    * Start workflow by message id
    * */
    public static void startProcessInstanceByMessage(String projectId, String messageEvent) {
        processManagerHashMap.get(projectId).launchProcessByMessageWithoutVariables(messageEvent);
    }
}
