package io.je.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.DynamicBpmnService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;

import io.je.callbacks.OnExecuteOperation;




public class ProcessManager {

	
	private static ProcessEngine processEngine; 
	
	private static RuntimeService runtimeService;
	
	private static TaskService taskService;
	
	private static ManagementService managementService;
	
	private static DynamicBpmnService dyService;
	
	private static RepositoryService repoService;
	
	private static ArrayList<String> processes = null;
	
	
	private static HashMap<String, OnExecuteOperation> allCallbacks = new HashMap<String, OnExecuteOperation>();
	
	public static void init() {
		
		 processEngine = ProcessEngines.getDefaultProcessEngine();
		 repoService = processEngine.getRepositoryService();
		 runtimeService = processEngine.getRuntimeService();
		 taskService = processEngine.getTaskService();
		 //taskService.createTaskQuery().taskId(id); not the same as execution.id this has to be the original task id from the bpmn so we can map them
	}
	
	public static void addProcess(String processId) {
		
		if(processes == null)  {
			processes = new ArrayList<String>();
		}
		processes.add(processId);
	}
	
	public static void registerWorkflowCallback(String id, OnExecuteOperation callback) {
		allCallbacks.put(id, callback);
	}
	
	public static void completeTask(Task task) {
		
		taskService.complete(task.getId());
	}
	
	public static void deployProcess(String classpathResource) {
		repoService.createDeployment()
	      .addClasspathResource(
	     classpathResource)
	      .deploy();
		
	}
	
	public static void launchProcessByKeyWithoutVariables(String id) {
		runtimeService.startProcessInstanceByKey(id);
	}
	
	public static void launchProcessByMessageWithoutVariables(String messageId) {
		
		runtimeService.startProcessInstanceByMessage(messageId);
	}
	
	public static void throwSignal(String signalId) {
		
		runtimeService.signalEventReceived(signalId);
	}
	
	public static void throwSignalInProcess(String signalId, String executionId) {
		
		runtimeService.signalEventReceived(signalId, executionId);
	}
	
	public static List<Execution> getAllSignalEventSubscriptions(String signalId) {
		
		return runtimeService.createExecutionQuery()
			      .signalEventSubscriptionName(signalId)
			      .list();
	}
	
	public static void throwMessageEvent(String messageId, String executionId) {
		
		runtimeService.messageEventReceived(messageId, executionId);
	}
	
	public static void throwMessageEvent(String messageId) {
		
		String executionId = runtimeService.createExecutionQuery()
			      .messageEventSubscriptionName(messageId).singleResult().getId();
		runtimeService.messageEventReceived(messageId, executionId);
	}
	
	public static Execution getMessageEventSubscription(String messageId) {
		
		return runtimeService.createExecutionQuery()
	      .messageEventSubscriptionName(messageId).singleResult();

	}
	
	/*public static void main(String[] args) {
		init();
		deployProcess("processes/test.bpmn");
		launchProcessByKeyWithoutVariables("Process_test");
		String taskId = getMessageEventSubscription("userStartWf").getId();
		throwMessageEvent("userStartWf",taskId);
		
	}*/

	public static HashMap<String, OnExecuteOperation> getAllCallbacks() {
		return allCallbacks;
	}

	public static ProcessEngine getProcessEngine() {
		return processEngine;
	}

	public static RuntimeService getRuntimeService() {
		return runtimeService;
	}

	public static TaskService getTaskService() {
		return taskService;
	}

	public static ManagementService getManagementService() {
		return managementService;
	}

	public static DynamicBpmnService getDyService() {
		return dyService;
	}

	public static RepositoryService getRepoService() {
		return repoService;
	}

	public static ArrayList<String> getProcesses() {
		return processes;
	}
}
