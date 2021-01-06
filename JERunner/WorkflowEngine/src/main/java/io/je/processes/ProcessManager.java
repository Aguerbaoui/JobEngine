package io.je.processes;

import io.je.JEProcess;
import io.je.callbacks.OnExecuteOperation;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import org.activiti.engine.*;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;

import java.util.HashMap;
import java.util.List;


public class ProcessManager {


    /*
     * Activiti Workflow engine
     * */
    private static ProcessEngine processEngine;

    /*
     * Runtime service for Activiti
     * */
    private static RuntimeService runtimeService;

    /*
     * Task service for Activiti
     * */
    private static TaskService taskService;


    /*
     * Management service for Activiti
     * */
    private static ManagementService managementService;

    /*
     * Dynamic service for Activiti
     * */
    private static DynamicBpmnService dyService;


    /*
     * Repository service for activiti
     * */
    private static RepositoryService repoService;


    /*
     * List of all active processes
     * */
    private static HashMap<String, JEProcess> processes = null;


    /*
     * List of all possible workflow task executions
     * */
    private static HashMap<String, OnExecuteOperation> allCallbacks = new HashMap<String, OnExecuteOperation>();


    /*
     * Initialize the workflow engine
     * */
    public static void init() {

        processEngine = ProcessEngines.getDefaultProcessEngine();
        repoService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        //taskService.createTaskQuery().taskId(id); not the same as execution.id this has to be the original task id from the bpmn so we can map them
    }

    /*
     * Add a process to engine
     * */
    public static void addProcess(JEProcess process) {

        if (processes == null) {
            processes = new HashMap<String, JEProcess>();
        }
        processes.put(process.getKey(), process);
    }

    /*
     * Register workflow execution callback
     * */
    public static void registerWorkflowCallback(String id, OnExecuteOperation callback) {
        allCallbacks.put(id, callback);
    }

    /*
     * Complete a user task
     * */
    public static void completeTask(Task task) {

        taskService.complete(task.getId());
    }

    /*
     * Deploy a process to engine
     * */
    public static void deployProcess(String classpathResource) {
        repoService.createDeployment()
                .addClasspathResource(
                        classpathResource)
                .deploy();

    }

    /*
     * Launch process by key without variables
     * */
    public static void launchProcessByKeyWithoutVariables(String id) throws WorkflowNotFoundException {
        if (processes.get(id) == null) {
            throw new WorkflowNotFoundException(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound);
        }
        runtimeService.startProcessInstanceByKey(id);
    }

    /*
     * Launch process by message event without variables
     * */
    public static void launchProcessByMessageWithoutVariables(String messageId) {

        runtimeService.startProcessInstanceByMessage(messageId);
    }

    /*
     * Throw signal in engine
     * */
    public static void throwSignal(String signalId) {

        runtimeService.signalEventReceived(signalId);
    }

    /*
     * Throw signal in workflow
     * */
    public static void throwSignalInProcess(String signalId, String executionId) {

        runtimeService.signalEventReceived(signalId, executionId);
    }

    /*
     * Returns a list of all signal event subscriptions
     * */
    public static List<Execution> getAllSignalEventSubscriptions(String signalId) {

        return runtimeService.createExecutionQuery()
                .signalEventSubscriptionName(signalId)
                .list();
    }

    /*
     * Throw a message event in workflow
     * */
    public static void throwMessageEvent(String messageId, String executionId) {

        runtimeService.messageEventReceived(messageId, executionId);
    }

    /*
     * Throw a message event
     * */
    public static void throwMessageEvent(String messageId) {

        String executionId = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageId).singleResult().getId();
        runtimeService.messageEventReceived(messageId, executionId);
    }

    /*
     * Returns the process execution subscribed to message event
     * */
    public static Execution getMessageEventSubscription(String messageId) {

        return runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageId).singleResult();

    }
	
	/*public static void main(String[] args) {
		init();
		deployProcess("processes/test.bpmn");
		try {
			launchProcessByKeyWithoutVariables("Process_test");
		} catch (WorkflowNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String taskId = getMessageEventSubscription("userStartWf").getId();
		throwMessageEvent("userStartWf",taskId);
		
	}*/

    /*
     * Get all execution callbacks
     * */
    public static HashMap<String, OnExecuteOperation> getAllCallbacks() {
        return allCallbacks;
    }

    /*
     * Get workflow Engine
     * */
    public static ProcessEngine getProcessEngine() {
        return processEngine;
    }

    /*
     * Get runtime service
     * */
    public static RuntimeService getRuntimeService() {
        return runtimeService;
    }

    /*
     * Get the engine task service
     * */
    public static TaskService getTaskService() {
        return taskService;
    }

    /*
     *Get the engine management service
     * */
    public static ManagementService getManagementService() {
        return managementService;
    }

    /*
     * Get the engine dynamic service
     * */
    public static DynamicBpmnService getDyService() {
        return dyService;
    }

    /*
     * Get the engine repository service
     * */
    public static RepositoryService getRepoService() {
        return repoService;
    }

    /*
     * Get all deployed processes
     * */
    public static HashMap<String, JEProcess> getProcesses() {
        return processes;
    }
}
