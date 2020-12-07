package io.je.runtime.workflow;

import io.je.processes.ProcessManager;

/*
 * Workflow Engine handler class 
 * */
public class WorkflowEngineHandler {

	/*
	 * Initialize workflow engine
	 * */
	public static void initWorkflowEngine() {

		ProcessManager.init();
	}

	/*
	 * Deploy bpmn process
	 * */
	public static void deployBPMN(String path) {
		ProcessManager.deployProcess(path);
	}

	/*
	 * Register workflow callbacks
	 * */
	public static void registerWorkflow(String processId) {
		ProcessManager.registerWorkflowCallback(processId, new WorkflowCallback());

	}

	/*
	 * Launch process without variables
	 * */
	public static void launchProcessWithoutVariables(String processId) {
		ProcessManager.launchProcessByKeyWithoutVariables(processId);
	}

	/*
	 * Add new process
	 * */
	public static void addProcess(String processId, String processPath) {

		registerWorkflow(processId);
		deployBPMN(processPath);

	}

	/*
	 * Trigger event by message
	 * */
	public static void triggerMessageEvent(String msg) {
		ProcessManager.throwMessageEvent(msg);
	}

	/*
	 * Main test
	 * */
	public static void main(String[] args) {
		initWorkflowEngine();
		addProcess("generatedBpmn", "processes/generatedBpmn.bpmn");
		launchProcessWithoutVariables("generatedBpmn");
		// triggerMessageEvent("userStartWf");

	}
}
