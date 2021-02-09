package io.je.project.controllers;

import io.je.project.models.WorkflowBlockModel;
import io.je.project.models.WorkflowModel;
import io.je.project.services.ProjectService;
import io.je.project.services.WorkflowService;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
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
import java.util.Arrays;

import static io.je.utilities.constants.ResponseMessages.*;

/*
 * Workflow builder Rest Controller
 * */
@RestController
@RequestMapping(value = "/workflow")
@CrossOrigin(maxAge = 3600)
public class WorkflowController {


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

            workflowService.addWorkflow(wf);
            projectService.saveProject(ProjectService.getProjectById(m.getProjectId()));
        } catch (ProjectNotFoundException e) {
            JELogger.error(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.PROJECT_NOT_FOUND));
        } catch (Exception e) {
            JELogger.error(ProjectController.class, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * Build workflow
     */
    @PostMapping(value = "/buildWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@PathVariable String projectId, @PathVariable String key) {

        try {
            workflowService.buildWorkflow(projectId, key);
        } catch (ProjectNotFoundException e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.PROJECT_NOT_FOUND));
        } catch (WorkflowNotFoundException e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.WORKFLOW_NOT_FOUND));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.NETWORK_ERROR, Errors.NETWORK_ERROR));
        } catch (Exception e) {
            JELogger.info(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_BUILT_SUCCESSFULLY));
    }

    /*
     * Run Workflow
     */
    @PostMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            workflowService.runWorkflow(projectId, key);
        } catch (ProjectNotFoundException | WorkflowNotFoundException | WorkflowAlreadyRunningException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        } catch (IOException e) {
            JELogger.trace(WorkflowController.class,  " Runner unreachable" );
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.JERUNNER_ERROR, Errors.JERUNNER_UNREACHABLE));
        }
        catch (Exception e) {
            JELogger.error(WorkflowController.class, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
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
            workflowService.removeWorkflow(projectId, workflowId);
            projectService.saveProject(ProjectService.getProjectById(projectId));
        } catch (ProjectNotFoundException | WorkflowNotFoundException e) {
            JELogger.error(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.WorkflowDeletionSucceeded));
    }

    /*
     * Delete a workflow
     */
    @PatchMapping(value = "/updateWorkflow/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("workflowId") String workflowId, @RequestBody WorkflowModel m) {

        try {
            workflowService.updateWorkflow(projectId, workflowId, m);
            projectService.saveProject(ProjectService.getProjectById(projectId));
        } catch (ProjectNotFoundException | WorkflowNotFoundException e) {
            JELogger.error(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    @GetMapping(value = "/getAllWorkflows/{projectId}")
    @ResponseBody
    public ResponseEntity<?> getAllWorkflows(@PathVariable("projectId") String projectId) {
        try {
            return ResponseEntity.ok(projectService.getAllWorkflows(projectId));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, Errors.PROJECT_NOT_FOUND));
        }
    }

    @GetMapping(value = "/getWorkflowById/{projectId}/{key}")
    @ResponseBody
    public ResponseEntity<?> getWorkflowById(@PathVariable("projectId") String projectId, @PathVariable("key") String key) {
        JEWorkflow w = null;
        try {
            w = projectService.getWorkflowById(projectId, key);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, Errors.PROJECT_NOT_FOUND));
        }
        if (w != null) {
            return ResponseEntity.ok(w);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.WORKFLOW_NOT_FOUND, Errors.WORKFLOW_NOT_FOUND));
    }

    /*
     * Add a new Workflow component
     */
    @PostMapping(value = "/addWorkflowBlock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflowBlock(@RequestBody WorkflowBlockModel block) {
        try {

            //block.setId(block.getId().replace("-", ""));
            workflowService.addWorkflowBlock(block);
            projectService.saveProject(ProjectService.getProjectById(block.getProjectId()));
        } catch (WorkflowNotFoundException | WorkflowBlockNotFound e) {
            JELogger.trace(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        } catch (Exception e) {
            JELogger.error(WorkflowController.class, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_COMPONENT_SUCCESSFULLY));
    }

    @PatchMapping(value = "/updateWorkflowBlock")
    public ResponseEntity<?> updateWorkflowBlock(@RequestBody WorkflowBlockModel block) {


        try {
            //block.setId(block.getId().replace("-", ""));
            workflowService.updateWorkflowBlock(block);
            projectService.saveProject(ProjectService.getProjectById(block.getProjectId()));
        } catch (WorkflowNotFoundException | WorkflowBlockNotFound e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

        } catch (Exception e) {
            JELogger.error(WorkflowController.class, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
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
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.WORKFLOW_NOT_FOUND));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.PROJECT_NOT_FOUND));
        } catch (WorkflowBlockNotFound e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.WORKFLOW_BLOCK_NOT_FOUND));
        } catch (InvalidSequenceFlowException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.INVALID_SEQUENCE_FLOW));
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
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.WORKFLOW_NOT_FOUND));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.PROJECT_NOT_FOUND));
        } catch (WorkflowBlockNotFound e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.WORKFLOW_BLOCK_NOT_FOUND));
        } catch (InvalidSequenceFlowException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.INVALID_SEQUENCE_FLOW));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, SEQUENCE_FLOW_DELETED_SUCCESSFULLY));

    }

    /*
     * add a new scripted Rule
     */
    @PostMapping(value = "/addBpmn/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId, @PathVariable("workflowId") String workflowId, @RequestBody String bpmn) {

        try {
            workflowService.addBpmn(projectId, workflowId, bpmn);
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        projectService.saveProject(ProjectService.getProjectById(projectId));

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * temporary function until autosave is implemented
     */
    @PostMapping(value = "/saveWorkflowFrontConfig/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveWorkflowFrontConfig(@PathVariable("projectId") String projectId,@PathVariable("workflowId") String workflowId, @RequestBody String config) {
        try {
            workflowService.setFrontConfig(projectId, workflowId, config);
        } catch (ProjectNotFoundException | WorkflowNotFoundException e) {
            JELogger.error(RuleController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, FRONT_CONFIG));
    }


}
