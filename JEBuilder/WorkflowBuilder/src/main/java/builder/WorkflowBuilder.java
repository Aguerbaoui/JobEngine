package builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import blocks.WorkflowBlock;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.AddWorkflowBlockException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import models.JEWorkflow;

/*
 * Workflow Builder class 
 * */
public class WorkflowBuilder {

	/*
	 * All Builder workflows
	 * */
	public static HashMap<String, HashMap<String, JEWorkflow>> workflows = new HashMap<String, HashMap<String, JEWorkflow>>(); 
	
	public static void addNewWorkflow(String projectId, String key) throws ProjectNotFoundException{
		if(workflows.get(projectId) == null) {
			// to be changed to throw project not found error
			workflows.put(projectId, new HashMap<String, JEWorkflow>());
		}
		
		if(workflows.get(projectId) == null) {
			// to be changed to throw project not found error
			throw new ProjectNotFoundException("2", Errors.projectNotFound);
		}
		JEWorkflow wf = new JEWorkflow();
		wf.setJobEngineElementID(key);
		wf.setJobEngineProjectID(projectId);
		workflows.get(projectId).put(key, wf);
		
	}
	
	/*
	 * Add a new workflow block
	 * */
	public static void addWorkflowBlock(WorkflowBlock block) throws AddWorkflowBlockException, ProjectNotFoundException {
		
		if(workflows.get(block.getJobEngineProjectID()) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}
		
		else if(workflows.get(block.getJobEngineProjectID()).get(block.getWorkflowId()) == null) {
			throw new AddWorkflowBlockException("1", Errors.getMessage(1));
		}
		
		workflows.get(block.getJobEngineProjectID()).get(block.getWorkflowId()).addBlock(block);
		
	}
	
	/*
	 * Add a new sequence flow
	 * */
	public static void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef, String condition) throws AddWorkflowBlockException{
		if(workflows.get(projectId) == null) {
			throw new AddWorkflowBlockException("2", Errors.getMessage(2));
		}
		
		else if(workflows.get(projectId).get(workflowId) == null) {
			throw new AddWorkflowBlockException("1", Errors.getMessage(1));
		}
		JELogger.info("pId " + projectId + " wId " + workflowId + " src " + sourceRef + " trgt " + targetRef);
		workflows.get(projectId).get(workflowId).addBlockFlow(sourceRef, targetRef, condition);
	}
	
	/*
	 * Build Workflow Bpmn
	 * */
	public static void buildWorkflow(String projectId, String key) throws WorkflowNotFoundException, ProjectNotFoundException{
		
		if(workflows.get(projectId) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}
		
		else if(workflows.get(projectId).get(key) == null) {
			throw new WorkflowNotFoundException("1", Errors.getMessage(1));
		}
		JEToBpmnMapper.launchBuildTest(workflows.get(projectId).get(key));
	}

	public static void runWorkflow(String key) throws IOException {
		Network.makeNetworkCall("http://127.0.0.1:8081/runWorkflow/" + key);
		// TODO Auto-generated method stub
		
	}
}
