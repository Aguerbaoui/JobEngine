package models;

import blocks.basic.StartBlock;
import io.je.utilities.runtimeobject.JEObject;

/*
 * Model class for a workflow
 * */
public class JEWorkflow extends JEObject{

	/*
	 * Workflow name
	 * */
	private String workflowName;
	
	/*
	 * Workflow start block
	 * */
	private StartBlock workflowStartBlock;
	
	/*
	 * Bpmn path
	 * */	
	private String bpmnPath;

	/*
	 * Return workflow name
	 * */
	public String getWorkflowName() {
		return workflowName;
	}

	/*
	 * set workflow name
	 * */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/*
	 * Return workflow start block
	 * */
	public StartBlock getWorkflowStartBlock() {
		return workflowStartBlock;
	}

	/*
	 * set workflow start block
	 * */
	public void setWorkflowStartBlock(StartBlock workflowStartBlock) {
		this.workflowStartBlock = workflowStartBlock;
	}

	/*
	 * Return bpmn path
	 * */
	public String getBpmnPath() {
		return bpmnPath;
	}

	/*
	 * set bpmn path
	 * */
	public void setBpmnPath(String bpmnPath) {
		this.bpmnPath = bpmnPath;
	} 
	
	
}
