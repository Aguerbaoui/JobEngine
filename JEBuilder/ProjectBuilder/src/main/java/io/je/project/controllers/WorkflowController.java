package io.je.project.controllers;

import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.MessageCatchEvent;
import io.je.project.models.WorkflowBlockModel;
import io.je.project.services.WorkflowService;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Workflow builder Rest Controller
 * */
@RestController("workflow")
public class WorkflowController {

    @Autowired
    WorkflowService workflowService;

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
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
                EndBlock b = new EndBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
                EventGatewayBlock b = new EventGatewayBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
                MessageCatchEvent b = new MessageCatchEvent();
                b.setName(block.getAttributes().get("name"));
                b.setMessageRef(block.getAttributes().get("messageRef"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
                ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
                ScriptBlock b = new ScriptBlock();
                b.setName(block.getAttributes().get("name"));
                b.setScript(block.getAttributes().get("script"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
                ParallelGatewayBlock b = new ParallelGatewayBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
                DBWriteBlock b = new DBWriteBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
                MailBlock b = new MailBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.addWorkflowBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.seqFlowType)) {
                workflowService.addSequenceFlow(block.getProjectId(), block.getWorkflowId(),
                        block.getAttributes().get("sourceRef"), block.getAttributes().get("targetRef"),
                        block.getAttributes().get("condition"));
            }

        } catch (AddWorkflowBlockException e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        } catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        }
        //JELogger.info(block.toString());
        return ResponseEntity.ok(new Response(200, "Added workflow component successfully"));
    }

    @PutMapping(value = "/updateWorkflowBlock/{key}")
    public ResponseEntity<?> updateWorkflowBlock(@PathVariable String key, @RequestBody WorkflowBlockModel block) {

        try {
            if (block.getType().equalsIgnoreCase(WorkflowConstants.startType)) {
                StartBlock b = new StartBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateStartBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.endType)) {
                EndBlock b = new EndBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateEndBlock(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.eventgatewayType)) {
                EventGatewayBlock b = new EventGatewayBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateEventGateway(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.messageintermediatecatcheventType)) {
                MessageCatchEvent b = new MessageCatchEvent();
                b.setName(block.getAttributes().get("name"));
                b.setMessageRef(block.getAttributes().get("messageRef"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateMessageCatchEvent(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.exclusivegatewayType)) {
                ExclusiveGatewayBlock b = new ExclusiveGatewayBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateExclusiveGateway(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.scripttaskType)) {
                ScriptBlock b = new ScriptBlock();
                b.setName(block.getAttributes().get("name"));
                b.setScript(block.getAttributes().get("script"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateScript(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.parallelgatewayType)) {
                ParallelGatewayBlock b = new ParallelGatewayBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateParallelGateway(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.dbservicetaskType)) {
                DBWriteBlock b = new DBWriteBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateDbTask(b);
            } else if (block.getType().equalsIgnoreCase(WorkflowConstants.mailservicetaskType)) {
                MailBlock b = new MailBlock();
                b.setName(block.getAttributes().get("name"));
                b.setJobEngineProjectID(block.getProjectId());
                b.setWorkflowId(block.getWorkflowId());
                b.setJobEngineElementID(block.getId());
                workflowService.updateMailTask(b);
            }

        } catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.ok(e.getMessage());
        }
        return ResponseEntity.ok(new Response(200, "Added workflow component successfully"));
    }

    /*
     * Delete a wokflow block
     * */
    @DeleteMapping(value = "/{projectId}/{key}/{id}")
    public ResponseEntity<?> deleteWorkflowBlock(@PathVariable String projectId, @PathVariable String key, @PathVariable String id) {

        try {
            workflowService.deleteWorkflowBlock(projectId, key, id);
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
            workflowService.deleteSequenceFlow(projectId, key, from, to);
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


}
