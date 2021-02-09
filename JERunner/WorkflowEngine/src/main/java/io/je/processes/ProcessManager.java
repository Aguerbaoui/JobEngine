package io.je.processes;

import io.je.JEProcess;
import io.je.callbacks.OnExecuteOperation;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.WorkflowAlreadyRunningException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.exceptions.WorkflwTriggeredByEventException;
import io.je.utilities.logger.JELogger;
import org.activiti.engine.*;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


public class ProcessManager {


    /*
     * Activiti Workflow engine
     * */
    private ProcessEngine processEngine;

    /*
     * Runtime service for Activiti
     * */
    private RuntimeService runtimeService;

    /*
     * Task service for Activiti
     * */
    private TaskService taskService;


    /*
     * Management service for Activiti
     * */
    private ManagementService managementService;

    /*
     * Dynamic service for Activiti
     * */
    private DynamicBpmnService dyService;


    /*
     * Repository service for activiti
     * */
    private RepositoryService repoService;


    /*
     * List of all active processes
     * */
    private HashMap<String, JEProcess> processes = new HashMap<>();


    /*
     * List of all possible workflow task executions
     * */
    private HashMap<String, OnExecuteOperation> allCallbacks = new HashMap<String, OnExecuteOperation>();


    /*
     * Initialize the workflow engine
     * */
    public ProcessManager() {

        processEngine = ProcessEngines.getDefaultProcessEngine();
        repoService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        //taskService.createTaskQuery().taskId(id); not the same as execution.id this has to be the original task id from the bpmn so we can map them
    }

    /*
     * Add a process to engine
     * */
    public void addProcess(JEProcess process) {

        if (processes == null) {
            processes = new HashMap<String, JEProcess>();
        }

        processes.put(process.getKey(), process);
    }

    /*
     * Register workflow execution callback
     * */
    public void registerWorkflowCallback(String id, OnExecuteOperation callback) {
        allCallbacks.put(id, callback);
    }

    /*
     * Complete a user task
     * */
    public void completeTask(Task task) {

        taskService.complete(task.getId());
    }

    /*
     * Deploy a process to engine
     * */
    public void deployProcess(String key) {
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
        //repoService.
        repoService.createDeployment()
                .addClasspathResource(
                         processes.get(key).getBpmnPath())
                .deploy();
        processes.get(key).setDeployed(true);

    }

    /*
     * Launch process by key without variables
     * */
    public void launchProcessByKeyWithoutVariables(String id) throws WorkflowNotFoundException, WorkflowAlreadyRunningException, WorkflwTriggeredByEventException {
        if (processes.get(id) == null) {
            throw new WorkflowNotFoundException( Errors.WORKFLOW_NOT_FOUND);
        }
        if(!processes.get(id).isRunning() && !processes.get(id).isTriggeredByEvent()) {
            processes.get(id).setRunning(true);
            runtimeService.startProcessInstanceByKey(id);
        }
    else { if(processes.get(id).isTriggeredByEvent()) {
            throw new WorkflwTriggeredByEventException(ResponseCodes.WORKFLOW_EVENT_TRIGGER, Errors.WORKFLOW_TRIGGERED_BY_EVENT);
        }
            throw new WorkflowAlreadyRunningException(Errors.WORKFLOW_ALREADY_RUNNING);
        }


    }

    /*
     * Launch process by message event without variables
     * */
    public void launchProcessByMessageWithoutVariables(String messageId) {

        runtimeService.startProcessInstanceByMessage(messageId);
    }

    /*
     * Throw signal in engine
     * */
    public void throwSignal(String signalId) {
       /* String executionId = runtimeService.createExecutionQuery()
                .signalEventSubscriptionName(signalId).singleResult().getId();*/
        runtimeService.signalEventReceived(signalId);
    }

    /*
     * Throw signal in workflow
     * */
    public void throwSignalInProcess(String signalId, String executionId) {

        runtimeService.signalEventReceived(signalId, executionId);
    }

    /*
     * Returns a list of all signal event subscriptions
     * */
    public List<Execution> getAllSignalEventSubscriptions(String signalId) {

        return runtimeService.createExecutionQuery()
                .signalEventSubscriptionName(signalId)
                .list();
    }

    /*
     * Throw a message event in workflow
     * */
    public void throwMessageEvent(String messageId, String executionId) {

        runtimeService.messageEventReceived(messageId, executionId);
    }

    /*
     * Throw a message event
     * */
    public void throwMessageEvent(String messageId) {

        String executionId = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageId).singleResult().getId();
        runtimeService.messageEventReceived(messageId, executionId);
    }

    /*
     * Returns the process execution subscribed to message event
     * */
    public Execution getMessageEventSubscription(String messageId) {

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
    public HashMap<String, OnExecuteOperation> getAllCallbacks() {
        return allCallbacks;
    }

    /*
     * Get workflow Engine
     * */
    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    /*
     * Get runtime service
     * */
    public RuntimeService getRuntimeService() {
        return runtimeService;
    }

    /*
     * Get the engine task service
     * */
    public TaskService getTaskService() {
        return taskService;
    }

    /*
     *Get the engine management service
     * */
    public ManagementService getManagementService() {
        return managementService;
    }

    /*
     * Get the engine dynamic service
     * */
    public DynamicBpmnService getDyService() {
        return dyService;
    }

    /*
     * Get the engine repository service
     * */
    public RepositoryService getRepoService() {
        return repoService;
    }

    /*
     * Get all deployed processes
     * */
    public HashMap<String, JEProcess> getProcesses() {
        return processes;
    }

   /* public void runAll() throws WorkflowNotFoundException {
        for(String key: processes.keySet()) {
            launchProcessByKeyWithoutVariables(key);
        }
    }*/

    public void runAll(String projectId) throws WorkflowNotFoundException{
        for(JEProcess process: processes.values()) {
            if(process.getProjectId().equals(projectId) && process.isDeployed() && !process.isRunning()) {
                try {
                    launchProcessByKeyWithoutVariables(process.getKey());
                } catch (WorkflowAlreadyRunningException | WorkflwTriggeredByEventException e) {
                    JELogger.error(ProcessManager.class, "Workflow running exception id = " + process.getKey());
                }
            }
        }
    }

    public void buildProjectWorkflows(String projectId) {
        for(JEProcess process: processes.values()) {
            if(process.getProjectId().equals(projectId) && !process.isDeployed()) {
                deployProcess(process.getKey());
            }
        }
    }

    public void stopProcess(String key) {
        if(processes.get(key).isRunning()) {
            runtimeService.deleteProcessInstance(key, "User Stopped the execution");
            processes.get(key).setRunning(false);
        }
    }

    public void stopProjectWorkflows(String projectId) {
        for(JEProcess process: processes.values()) {
            if(process.getProjectId().equals(projectId) && process.isRunning()) {
                runtimeService.deleteProcessInstance(process.getKey(), "User Stopped the execution");
            }
        }
    }

    public void setRunning(String id, boolean b) {
        processes.get(id.replace(id.substring(id.indexOf(':'), id.length()), "")).setRunning(b);
    }

}
