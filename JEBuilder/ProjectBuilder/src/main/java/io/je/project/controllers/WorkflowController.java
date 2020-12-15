package io.je.project.controllers;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
import builder.WorkflowBuilder;
import io.je.project.models.WorkflowBlockModel;
import io.je.project.models.WorkflowModel;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.AddWorkflowBlockException;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;

/*
 * Workflow builder Rest Controller
 * */
@RestController
public class WorkflowController {

	/*
	 * Add a new workflow from front
	 */
	@PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel m) {
		try {
			WorkflowBuilder.addNewWorkflow(m.getProjectId(), m.getKey());
		}

		catch (ProjectNotFoundException e) {
			JELogger.info(e.getMessage());
			return ResponseEntity.ok(Errors.projectNotFound);
		} catch (Exception e) {
			JELogger.info(e.getMessage());
			return ResponseEntity.ok(Errors.uknownError);
		}
		JELogger.info("Added workflow successfully");
		return ResponseEntity.ok("Added workflow successfully");
	}

	/*
	 * Add a new Workflow component
	 */
	@PostMapping(value = "/addWorkflowBlock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addWorkflowBlock(@RequestBody WorkflowBlockModel block) {
		try {
			if (block.getType().equalsIgnoreCase(WorkflowConstants.startType)) {
				StartBlock b = new StartBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
				EndBlock b = new EndBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
				EventGatewayBlock b = new EventGatewayBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
				MessageCatchEvent b = new MessageCatchEvent();
				b.setName(block.getAttributes().get("name"));
				b.setMessageRef(block.getAttributes().get("messageRef"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
				ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
				ScriptBlock b = new ScriptBlock();
				b.setName(block.getAttributes().get("name"));
				b.setScript(block.getAttributes().get("script"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
				ParallelGatewayBlock b = new ParallelGatewayBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
				DBWriteBlock b = new DBWriteBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
				MailBlock b = new MailBlock();
				b.setName(block.getAttributes().get("name"));
				b.setJobEngineProjectID(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setJobEngineElementID(block.getId());
				WorkflowBuilder.addWorkflowBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.seqFlowType)) {
				WorkflowBuilder.addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
						block.getAttributes().get("sourceRef"), block.getAttributes().get("targetRef"),
						block.getAttributes().get("condition"));
			}

		} catch (AddWorkflowBlockException e) {
			JELogger.info(e.getMessage());
			return ResponseEntity.ok(e.getMessage());
		} catch (Exception e) {
			JELogger.info(e.getMessage());
			e.printStackTrace();
			return ResponseEntity.ok(e.getMessage());
		}
		//JELogger.info(block.toString());
		return ResponseEntity.ok("Added workflow component successfully");

	}

	@PutMapping(value = "/updateWorkflowBlock/{key}")
	public ResponseEntity<?> updateWorkflowBlock(@PathVariable String key, @RequestBody WorkflowBlockModel block) {
		 
		try {
			if (block.getType().equalsIgnoreCase(WorkflowConstants.startType)) {
				StartBlock b = new StartBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateStartBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
				EndBlock b = new EndBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateEndBlock(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
				EventGatewayBlock b = new EventGatewayBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateEventGateway(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
				MessageCatchEvent b = new MessageCatchEvent();
				b.setName(block.getAttributes().get("name"));
				b.setMessageRef(block.getAttributes().get("messageRef"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateMessageCatchEvent(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
				ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateExclusiveGateway(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
				ScriptBlock b = new ScriptBlock();
				b.setName(block.getAttributes().get("name"));
				b.setScript(block.getAttributes().get("script"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateScript(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
				ParallelGatewayBlock b = new ParallelGatewayBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateParallelGateway(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
				DBWriteBlock b = new DBWriteBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateDbTask(b);
			} else if (block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
				MailBlock b = new MailBlock();
				b.setName(block.getAttributes().get("name"));
				b.setProjectId(block.getProjectId());
				b.setWorkflowId(block.getWorkflowId());
				b.setId(block.getId());
				WorkflowBuilder.updateMailTask(b);
			}

		} catch (Exception e) {
			JELogger.info(e.getMessage());
			e.printStackTrace();
			return ResponseEntity.ok(e.getMessage());
		}
		return ResponseEntity.ok("Updated workflow component successfully");
	}

	/*
	 * Delete a wokflow block
	 * */
	@DeleteMapping(value = "/{projectId}/{key}/{id}")
	public ResponseEntity<?> deleteWorkflowBlock(@PathVariable String projectId, @PathVariable String key, @PathVariable String id) {
	
			try {
				WorkflowBuilder.deleteWorkflowBlock(projectId, key, id);
			} catch (WorkflowNotFoundException e) {
				return ResponseEntity.ok(Errors.workflowNotFound);
			} catch (ProjectNotFoundException e) {
				return ResponseEntity.ok(Errors.projectNotFound);
			} catch (WorkflowBlockNotFound e) {
				return ResponseEntity.ok(Errors.workflowBloclNotFound);
			} catch (InvalidSequenceFlowException e) {
				return ResponseEntity.ok(Errors.InvalidSequenceFlow);
			}
		
			return ResponseEntity.ok("Block deleted successfully");
	}
	
	/*
	 * Delete a sequence flow in a workflow
	 * */
	@DeleteMapping(value = "/{projectId}/{key}/{from}/{to}")
	public ResponseEntity<?> deleteSequenceFlow(@PathVariable String projectId, @PathVariable String key, @PathVariable String from, @PathVariable String to) {
	
			try {
				WorkflowBuilder.deleteSequenceFlow(projectId, key, from, to);
			} catch (WorkflowNotFoundException e) {
				return ResponseEntity.ok(Errors.workflowNotFound);
			} catch (ProjectNotFoundException e) {
				return ResponseEntity.ok(Errors.projectNotFound);
			} catch (WorkflowBlockNotFound e) {
				return ResponseEntity.ok(Errors.workflowBloclNotFound);
			} catch (InvalidSequenceFlowException e) {
				return ResponseEntity.ok(Errors.InvalidSequenceFlow);
			}
		
			return ResponseEntity.ok("Sequence flow deleted successfully");
	}
	
	/*
	 * Build workflow
	 * */
	@PostMapping(value = "/buildWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildWorkflow(@RequestBody WorkflowModel m) {
		
		try {
			WorkflowBuilder.buildWorkflow(m.getProjectId(), m.getKey());
		}
		catch(ProjectNotFoundException e) {
			JELogger.info(e.getMessage());
			return ResponseEntity.ok(Errors.projectNotFound);
		} catch (WorkflowNotFoundException e) {
			JELogger.info(e.getMessage());
			return ResponseEntity.ok(Errors.workflowNotFound);
		}
		return ResponseEntity.ok("Workflow built successfully");
	}

	/*
	 * Run Workflow
	 * */
	@PostMapping(value = "/runWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runWorkflow(@PathVariable String key) {
		try {
			JELogger.info(key);
			WorkflowBuilder.runWorkflow(key);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.ok("Error executing workflow");
		}
		return ResponseEntity.ok("Executing workflow");
	}
}
