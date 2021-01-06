package io.je.project.controllers;

import io.je.project.models.WorkflowBlockModel;
import io.je.project.services.ProjectService;
import io.je.project.services.WorkflowService;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.InvalidSequenceFlowException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.WorkflowBlockNotFound;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Workflow builder Rest Controller
 * */
@RestController
@RequestMapping(value= "/workflow")
public class WorkflowController {

    public static final String ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY = "Added workflow component successfully";
    public static final String SEQUENCE_FLOW_DELETED_SUCCESSFULLY = "Sequence flow deleted successfully";
    public static final String BLOCK_DELETED_SUCCESSFULLY = "Block deleted successfully";

    @Autowired
    WorkflowService workflowService;

    @Autowired
    ProjectService projectService;
    /*
     * Add a new Workflow component
     */
    @PostMapping(value = "/addWorkflowBlock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflowBlock(@RequestBody WorkflowBlockModel block) {
        try {

            workflowService.addWorkflowBlock(block);
            projectService.saveProject(ProjectService.getProjectById(block.getProjectId()));
        } catch (WorkflowNotFoundException|WorkflowBlockNotFound  e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new Response(e.getCode(), e.getMessage()));

        }
        catch (Exception e) {
            e.printStackTrace();
            JELogger.info(WorkflowController.class, APIConstants.UNKNOWN_ERROR);
            return ResponseEntity.badRequest().body(new Response(APIConstants.UNKNOWN_ERROR, Errors.uknownError));
        }

        return ResponseEntity.ok(new Response(APIConstants.CODE_OK, ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY));
    }

    @PutMapping(value = "/updateWorkflowBlock")
    public ResponseEntity<?> updateWorkflowBlock( @RequestBody WorkflowBlockModel block) {


        try {
            workflowService.updateWorkflowBlock(block);
            projectService.saveProject(ProjectService.getProjectById(block.getProjectId()));
        }
        catch (WorkflowNotFoundException|WorkflowBlockNotFound  e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new Response(e.getCode(), e.getMessage()));

        }
        catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Response(APIConstants.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new Response(APIConstants.CODE_OK, ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY));
    }

    /*
     * Delete a wokflow block
     * */
    @DeleteMapping(value = "deleteWorkflowBlock/{projectId}/{key}/{id}")
    public ResponseEntity<?> deleteWorkflowBlock(@PathVariable String projectId, @PathVariable String key, @PathVariable String id) {

        try {
            workflowService.deleteWorkflowBlock(projectId, key, id);
            projectService.saveProject(ProjectService.getProjectById(projectId));
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound));
        } catch (WorkflowBlockNotFound e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.WORKFLOW_BLOCK_NOT_FOUND, Errors.workflowBlockNotFound));
        } catch (InvalidSequenceFlowException e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.INVALID_SEQUENCE_FLOW, Errors.InvalidSequenceFlow));
        }
        return ResponseEntity.ok(new Response(APIConstants.CODE_OK, BLOCK_DELETED_SUCCESSFULLY));
    }

    /*
     * Delete a sequence flow in a workflow
     * */
    @DeleteMapping(value = "deleteSequenceFlow/{projectId}/{key}/{from}/{to}")
    public ResponseEntity<?> deleteSequenceFlow(@PathVariable String projectId, @PathVariable String key, @PathVariable String from, @PathVariable String to) {

        try {
            workflowService.deleteSequenceFlow(projectId, key, from, to);
            projectService.saveProject(ProjectService.getProjectById(projectId));
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.WORKFLOW_NOT_FOUND, Errors.workflowNotFound));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.PROJECT_NOT_FOUND, Errors.projectNotFound));
        } catch (WorkflowBlockNotFound e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.WORKFLOW_BLOCK_NOT_FOUND, Errors.workflowBlockNotFound));
        } catch (InvalidSequenceFlowException e) {
            return ResponseEntity.badRequest().body(new Response(APIConstants.INVALID_SEQUENCE_FLOW, Errors.InvalidSequenceFlow));
        }
        return ResponseEntity.ok(new Response(APIConstants.CODE_OK, SEQUENCE_FLOW_DELETED_SUCCESSFULLY));

    }


}
