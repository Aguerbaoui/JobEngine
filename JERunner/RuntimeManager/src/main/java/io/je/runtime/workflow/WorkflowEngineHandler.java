package io.je.runtime.workflow;

import io.je.JEProcess;
import io.je.processes.ProcessManager;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;

/*
 * Workflow Engine handler class 
 * */
public class WorkflowEngineHandler {

	private static boolean init = false;
	/*
	 * Initialize workflow engine
	 * */
	public static void initWorkflowEngine() {

		ProcessManager.init();
		init = true;
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
	public static void launchProcessWithoutVariables(String processId) throws WorkflowNotFoundException {
		JELogger.info("running workflow " + processId);
		ProcessManager.launchProcessByKeyWithoutVariables(processId);
	}

	/*
	 * Add new process
	 * */
	public static void addProcess(String processId, String processPath) {

		if(!init) initWorkflowEngine();
		ProcessManager.addProcess(new JEProcess(processId, processId, processPath));
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
		JELogger.info("running workflow ");
		addProcess("testGenerated", "processes/testGenerated.bpmn");
		try {
			launchProcessWithoutVariables("testGenerated");
		} catch (WorkflowNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
