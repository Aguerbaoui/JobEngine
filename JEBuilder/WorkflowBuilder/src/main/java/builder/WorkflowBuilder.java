package builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import blocks.WorkflowBlock;
import blocks.basic.DBWriteBlock;
import blocks.basic.EndBlock;
import blocks.basic.MailBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.StartBlock;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.MessageCatchEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.AddWorkflowBlockException;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
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
	 */
	public static HashMap<String, HashMap<String, JEWorkflow>> workflows = new HashMap<String, HashMap<String, JEWorkflow>>();

	public static void addNewWorkflow(String projectId, String key) throws ProjectNotFoundException {
		if (workflows.get(projectId) == null) {
			// to be changed to throw project not found error
			workflows.put(projectId, new HashMap<String, JEWorkflow>());
		}

		if (workflows.get(projectId) == null) {
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
	 */
	public static void addWorkflowBlock(WorkflowBlock block)
			throws AddWorkflowBlockException, ProjectNotFoundException {

		if (workflows.get(block.getProjectId()) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}

		else if (workflows.get(block.getProjectId()).get(block.getWorkflowId()) == null) {
			throw new AddWorkflowBlockException("1", Errors.getMessage(1));
		}

		workflows.get(block.getProjectId()).get(block.getWorkflowId()).addBlock(block);

	}
	
	/*
	 * Delete an exisiting workflow block
	 * */
	public static void deleteWorkflowBlock(String projectId, String key, String id) throws WorkflowNotFoundException, ProjectNotFoundException, WorkflowBlockNotFound, InvalidSequenceFlowException{
		if (workflows.get(projectId) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}

		else if (workflows.get(projectId).get(key) == null) {
			throw new WorkflowNotFoundException("1", Errors.getMessage(1));
		}
		else if (!blockExists(projectId, key, id)) {
			throw new WorkflowBlockNotFound("3", Errors.getMessage(3));
		}
		
		workflows.get(projectId).get(key).deleteWorkflowBlock(id);
	}

	/*
	 * Delete a sequence flow
	 */
	public static void deleteSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef)
			throws WorkflowNotFoundException, ProjectNotFoundException, InvalidSequenceFlowException, WorkflowBlockNotFound {
		if (workflows.get(projectId) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}

		else if (workflows.get(projectId).get(workflowId) == null) {
			throw new WorkflowNotFoundException("1", Errors.getMessage(1));
		}

		else if (workflows.get(projectId).get(workflowId).getAllBlocks().get(sourceRef) == null
				|| workflows.get(projectId).get(workflowId).getAllBlocks().get(targetRef) == null) {
			throw new WorkflowBlockNotFound("3", Errors.getMessage(3));
		}

		workflows.get(projectId).get(workflowId).deleteSequenceFlow(sourceRef, targetRef);
	}

	/*
	 * Add a new sequence flow
	 */
	public static void addSequenceFlow(String projectId, String workflowId, String sourceRef, String targetRef,
			String condition) throws AddWorkflowBlockException {
		if (workflows.get(projectId) == null) {
			throw new AddWorkflowBlockException("2", Errors.getMessage(2));
		}

		else if (workflows.get(projectId).get(workflowId) == null) {
			throw new AddWorkflowBlockException("1", Errors.getMessage(1));
		}

		else if (workflows.get(projectId).get(workflowId).getAllBlocks().get(sourceRef) == null
				|| workflows.get(projectId).get(workflowId).getAllBlocks().get(targetRef) == null) {
			throw new AddWorkflowBlockException("3", Errors.getMessage(3));
		}

		JELogger.info("pId " + projectId + " wId " + workflowId + " src " + sourceRef + " trgt " + targetRef);
		workflows.get(projectId).get(workflowId).addBlockFlow(sourceRef, targetRef, condition);
	}

	/*
	 * Build Workflow Bpmn
	 */
	public static void buildWorkflow(String projectId, String key)
			throws WorkflowNotFoundException, ProjectNotFoundException {

		if (workflows.get(projectId) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}

		else if (workflows.get(projectId).get(key) == null) {
			throw new WorkflowNotFoundException("1", Errors.getMessage(1));
		}
		JEToBpmnMapper.launchBuildTest(workflows.get(projectId).get(key));
	}

	/*
	 * Run workflow in runtime engine
	 * */
	public static void runWorkflow(String key) throws IOException {
		Network.makeNetworkCall("http://127.0.0.1:8081/runWorkflow/" + key);
		// TODO Auto-generated method stub

	}

	/*
	 * Check if a block exists in a workflow
	 * */
	public static boolean blockExists(String projectId, String key, String blockId) {
		return workflows.get(projectId).get(key).getAllBlocks().containsKey(blockId);
	}

	/*
	 * Update an existing start block
	 * */
	public static void updateStartBlock(StartBlock b)
			throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound {
		if (workflows.get(b.getProjectId()) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}

		else if (workflows.get(b.getProjectId()).get(b.getWorkflowId()) == null) {
			throw new WorkflowNotFoundException("1", Errors.getMessage(1));
		}
		if (!blockExists(b.getProjectId(), b.getWorkflowId(), b.getId())) {
			throw new WorkflowBlockNotFound("3", Errors.getMessage(3));
		}

		StartBlock existantBlock = (StartBlock) workflows.get(b.getProjectId()).get(b.getWorkflowId()).getAllBlocks().get(b.getId());
		existantBlock.setName(b.getName());
	}

	/*
	 * Update an existing end block
	 * */
	public static void updateEndBlock(EndBlock b) throws ProjectNotFoundException, WorkflowNotFoundException, WorkflowBlockNotFound{
		if (workflows.get(b.getProjectId()) == null) {
			throw new ProjectNotFoundException("2", Errors.getMessage(2));
		}

		else if (workflows.get(b.getProjectId()).get(b.getWorkflowId()) == null) {
			throw new WorkflowNotFoundException("1", Errors.getMessage(1));
		}
		if (!blockExists(b.getProjectId(), b.getWorkflowId(), b.getId())) {
			throw new WorkflowBlockNotFound("3", Errors.getMessage(3));
		}
		
		workflows.get(b.getProjectId()).get(b.getWorkflowId()).getAllBlocks().get(b.getId()).setName(b.getName());
		workflows.get(b.getProjectId()).get(b.getWorkflowId()).getAllBlocks().get(b.getId()).setCondition(b.getCondition());
		
	}

	public static void updateEventGateway(EventGatewayBlock b) {
		// TODO Auto-generated method stub
		
	}

	public static void updateMessageCatchEvent(MessageCatchEvent b) {
		// TODO Auto-generated method stub
		
	}

	public static void updateExclusiveGateway(ExclusiveGatewayBlock b) {
		// TODO Auto-generated method stub
		
	}

	public static void updateScript(ScriptBlock b) {
		// TODO Auto-generated method stub
		
	}

	public static void updateParallelGateway(ParallelGatewayBlock b) {
		// TODO Auto-generated method stub
		
	}

	public static void updateMailTask(MailBlock b) {
		// TODO Auto-generated method stub
		
	}

	public static void updateDbTask(DBWriteBlock b) {
		// TODO Auto-generated method stub
		
	}
}
