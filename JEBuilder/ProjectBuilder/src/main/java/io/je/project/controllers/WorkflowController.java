package io.je.project.controllers;

import io.je.project.models.WorkflowBlockModel;
import io.je.project.models.WorkflowModel;
import io.je.project.services.ProjectService;
import io.je.project.services.WorkflowService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

import static io.je.utilities.constants.ResponseMessages.*;

/*
 * Workflow builder Rest Controller
 * */
@RestController
@RequestMapping(value= "/workflow")
@CrossOrigin(maxAge = 3600)
public class WorkflowController {

    public static final String ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY = "Added workflow component successfully";
    public static final String SEQUENCE_FLOW_DELETED_SUCCESSFULLY = "Sequence flow deleted successfully";
    public static final String BLOCK_DELETED_SUCCESSFULLY = "Block deleted successfully";

    @Autowired
    WorkflowService workflowService;

    @Autowired
    ProjectService projectService;

    @PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel m) {
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementID(m.getKey());
        wf.setJobEngineProjectID(m.getProjectId());
        wf.setWorkflowName(m.getName());
        try {
            projectService.addWorkflowToProject(wf);
        } catch (ProjectNotFoundException e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (Exception e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * Build workflow
     */
    @PostMapping(value = "/buildWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@RequestBody WorkflowModel m) {

        try {
            projectService.buildWorkflow(m.getProjectId(), m.getKey());
        } catch (ProjectNotFoundException e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (WorkflowNotFoundException e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowNotFound));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.NETWORK_ERROR, Errors.NETWORK_ERROR));
        } catch (Exception e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_BUILT_SUCCESSFULLY));
    }

    /*
     * Run Workflow
     */
    @PostMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            projectService.runWorkflow(projectId, key);
        } catch (ProjectNotFoundException | WorkflowNotFoundException | WorkflowAlreadyRunningException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }

    /*
     * Delete a workflow
     */
    @DeleteMapping(value = "/deleteWorkflow/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("workflowId") String workflowId) {

        try {
            projectService.deleteWorkflowFromProject(projectId, workflowId);
            //projectService.saveProject(ProjectService.getProjectById(projectId));
        } catch (ProjectNotFoundException | WorkflowNotFoundException e) {
            JELogger.error(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.WorkflowDeletionSucceeded));
    }

    @GetMapping(value = "/getAllWorkflows/{projectId}")
    @ResponseBody
    public ResponseEntity<?> getAllWorkflows(@PathVariable("projectId") String projectId) {
        try {
            return ResponseEntity.ok(projectService.getAllWorkflows(projectId));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.ok(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, Errors.projectNotFound));
        }
    }

    @GetMapping(value = "/getWorkflowById/{projectId}/{key}")
    @ResponseBody
    public ResponseEntity<?> getWorkflowById(@PathVariable("projectId") String projectId, @PathVariable("key") String key) {
        JEWorkflow w = null;
        try {
            w = projectService.getWorkflowById(projectId, key);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.ok(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, Errors.projectNotFound));
        }
        if(w != null) {
            return ResponseEntity.ok(w);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.WORKFLOW_NOT_FOUND, Errors.workflowNotFound));
    }

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
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        }
        catch (Exception e) {
            e.printStackTrace();
            JELogger.info(WorkflowController.class, Errors.uknownError);
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY));
    }

    @PutMapping(value = "/updateWorkflowBlock")
    public ResponseEntity<?> updateWorkflowBlock( @RequestBody WorkflowBlockModel block) {


        try {
            workflowService.updateWorkflowBlock(block);
            projectService.saveProject(ProjectService.getProjectById(block.getProjectId()));
        }
        catch (WorkflowNotFoundException|WorkflowBlockNotFound  e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        }
        catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY));
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
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowNotFound));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (WorkflowBlockNotFound e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowBlockNotFound));
        } catch (InvalidSequenceFlowException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.InvalidSequenceFlow));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BLOCK_DELETED_SUCCESSFULLY));
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
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowNotFound));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (WorkflowBlockNotFound e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowBlockNotFound));
        } catch (InvalidSequenceFlowException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.InvalidSequenceFlow));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, SEQUENCE_FLOW_DELETED_SUCCESSFULLY));

    }

    /*
     * add a new scripted Rule
     */
    @PostMapping(value = "/addBpmn/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId, @PathVariable("workflowId") String workflowId,@RequestBody String bpmn) {

        try {
            workflowService.addBpmn(projectId,workflowId,bpmn);
        } catch (ProjectNotFoundException  e) {
            e.printStackTrace();
            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        projectService.saveProject(ProjectService.getProjectById(projectId));

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.ADDED_WORKFLOW_SUCCESSFULLY));
    }

}
