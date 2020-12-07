package io.je.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import blocks.WorkflowBlock;
import blocks.basic.DBWriteBlock;
import blocks.basic.EndBlock;
import blocks.basic.MailBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.SequenceFlowBlock;
import blocks.basic.StartBlock;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.MessageCatchEvent;
import builder.WorkflowBuilder;
import io.je.project.models.WorkflowBlockModel;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.AddWorkflowBlockException;
import io.je.utilities.logger.JELogger;


/*
 * Workflow builder Rest Controller
 * */
@RestController
public class WorkflowController {

	
	/*
	 * Add a new Workflow component
	 * */
	@PostMapping(value = "/addWorkflowBlock", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addWorkflow(@RequestBody WorkflowBlockModel block) {	
		try {
		if(block.getType().equalsIgnoreCase(WorkflowConstants.startType)) {
			StartBlock b = new StartBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
			EndBlock b = new EndBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
			EventGatewayBlock b = new EventGatewayBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
			MessageCatchEvent b = new MessageCatchEvent();
			b.setName(block.getAttributes().get("name"));
			b.setMessageRef(block.getAttributes().get("messageRef"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
			ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
			ScriptBlock b = new ScriptBlock();
			b.setName(block.getAttributes().get("name"));
			b.setScript(block.getAttributes().get("script"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
			ParallelGatewayBlock b = new ParallelGatewayBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
			DBWriteBlock b = new DBWriteBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
			MailBlock b = new MailBlock();
			b.setName(block.getAttributes().get("name"));
			b.setProjectId(block.getProjectId());
			b.setId(block.getId());
			WorkflowBuilder.addWorkflowBlock(b);
		}
		else if(block.getType().equalsIgnoreCase(WorkflowConstants.seqFlowType)) {
			WorkflowBuilder.addSequenceFlow(block.getProjectId(), block.getId(), 
					block.getAttributes().get("sourceRef"), block.getAttributes().get("targetRef"), block.getAttributes().get("condition"));
		}
		
		}
		catch(AddWorkflowBlockException e) {
			JELogger.info(e.getMessage());
			return ResponseEntity.ok(e.getMessage());
		}
		JELogger.info(block.toString());
		return ResponseEntity.ok("Added workflow component successfully");
		
	}
	
}
