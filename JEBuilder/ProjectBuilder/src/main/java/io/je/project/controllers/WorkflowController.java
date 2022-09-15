package io.je.project.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ProjectService;
import io.je.project.services.WorkflowService;
import io.je.utilities.beans.JECustomResponse;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.models.LibModel;
import io.je.utilities.models.WorkflowBlockModel;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.ruleutils.OperationStatusDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static io.je.utilities.constants.JEMessages.*;

/*
 * Workflow builder Rest Controller
 * */
@RestController
@RequestMapping(value = "/workflow")
@CrossOrigin(maxAge = 3600)
public class WorkflowController {

    @Autowired
    @Lazy
    WorkflowService workflowService;

    @Autowired
    @Lazy
    ProjectService projectService;


    /*
     * Add workflow
     */
    @PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel m) {
        try {
            projectService.getProject(m.getProjectId());

            workflowService.addWorkflow(m);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * Build workflow
     */
    @PostMapping(value = "/buildWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@PathVariable String projectId, @PathVariable String key) {

        try {
            projectService.getProject(projectId);
            OperationStatusDetails result = workflowService.buildWorkflow(projectId, key).get();
            if (result.isOperationSucceeded()) {
                return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_BUILT_SUCCESSFULLY));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JEResponse(ResponseCodes.WORKFLOW_BUILD_ERROR, result.getOperationError()));

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

    }

    /*
     * Run Workflow
     */
    @PostMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            projectService.getProject(projectId);
            OperationStatusDetails result = workflowService.runWorkflow(projectId, key)
                    .get();
            if (result.isOperationSucceeded()) {
                return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JEResponse(ResponseCodes.WORKFLOW_RUN_ERROR, WORKFLOW_BUILD_ERROR));

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

    }

    /*
     * Stop Workflow
     */
    @PostMapping(value = "/stopWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            projectService.getProject(projectId);
            OperationStatusDetails result = workflowService.stopWorkflow(projectId, key)
                    .get();
            if (result.isOperationSucceeded()) {
                return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_STOPPED_SUCCESSFULLY));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JEResponse(ResponseCodes.WORKFLOW_DELETION_ERROR, result.getOperationError()));

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
    }

    /*
     * Delete workflow
     */
    @DeleteMapping(value = "/deleteWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("key") String key) {

        try {
            projectService.getProject(projectId);

            workflowService.removeWorkflow(projectId, key);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.WORKFLOW_DELETED_SUCCESSFULLY));
    }

    /*
     * Update workflow
     */
    @PatchMapping(value = "/updateWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("key") String key, @RequestBody WorkflowModel m) {

        try {
            projectService.getProject(projectId);

            workflowService.updateWorkflow(projectId, key, m);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    /*
     * Get all workflows
     */
    @GetMapping(value = "/getAllWorkflows/{projectId}")
    @ResponseBody
    public ResponseEntity<?> getAllWorkflows(@PathVariable("projectId") String projectId) {
        try {
            projectService.getProject(projectId);

            return ResponseEntity.ok(workflowService.getAllWorkflows(projectId)
                    .get());
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
    }

    /*
     * Update a workflow status
     */
    @PatchMapping(value = "/updateStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateStatus(@RequestBody WorkflowModel m) {

        try {
            projectService.getProject(m.getProjectId());

            workflowService.updateWorkflowStatus(m.getProjectId(), m.getId(), m.getStatus());
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    /*
     * Get a workflow
     */
    @GetMapping(value = "/getWorkflowById/{projectId}/{workflowId}")
    @ResponseBody
    public ResponseEntity<?> getWorkflowById(@PathVariable("projectId") String projectId,
                                             @PathVariable("workflowId") String workflowId) {
        WorkflowModel w = null;
        try {
            projectService.getProject(projectId);

            w = workflowService.getWorkflow(workflowId)
                    .get();
            return ResponseEntity.ok(w);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
    }

    /*
     * Add a new block to workflow
     */
    @PostMapping(value = "/addWorkflowBlock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflowBlock(@RequestBody WorkflowBlockModel block) {
        String generatedBlockName = "";

        try {
            projectService.getProject(block.getProjectId());

            generatedBlockName = workflowService.addWorkflowBlock(block);
            projectService.saveProject(block.getProjectId());

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        HashMap<String, String> object = new HashMap<>();

        object.put("blockName", generatedBlockName);
        return ResponseEntity.ok(object);
    }

    /*
     * Update a workflow block
     */
    @PatchMapping(value = "/updateWorkflowBlock")
    public ResponseEntity<?> updateWorkflowBlock(@RequestBody WorkflowBlockModel block) {

        try {
            projectService.getProject(block.getProjectId());
            workflowService.updateWorkflowBlock(block);
            projectService.saveProject(block.getProjectId());

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    /*
     * Delete a workflow block
     */
    @DeleteMapping(value = "deleteWorkflowBlock/{projectId}/{key}/{id}") // FIXME need slash at the beginning?
    public ResponseEntity<?> deleteWorkflowBlock(@PathVariable String projectId, @PathVariable String key,
                                                 @PathVariable String id) {

        try {
            projectService.getProject(projectId);

            workflowService.deleteWorkflowBlock(projectId, key, id);

            projectService.saveProject(projectId);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BLOCK_DELETED_SUCCESSFULLY));
    }

    /**
     * Delete a sequence flow in a workflow
     *
     * @param projectId
     * @param key
     * @param from
     * @param to
     * @return
     */
    @DeleteMapping(value = "deleteSequenceFlow/{projectId}/{key}/{from}/{to}") // FIXME need slash at the beginning?
    public ResponseEntity<?> deleteSequenceFlow(@PathVariable String projectId, @PathVariable String key,
                                                @PathVariable String from, @PathVariable String to) {

        try {
            projectService.getProject(projectId);

            workflowService.deleteSequenceFlow(projectId, key, from, to);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, SEQUENCE_FLOW_DELETED_SUCCESSFULLY));

    }

    /**
     * Not used : add a new scripted Workflow
     */

    @PostMapping(value = "/addBpmn/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId,
                                             @PathVariable("key") String key, @RequestBody String bpmn) {

        try {
            projectService.getProject(projectId);

            workflowService.addBpmn(projectId, key, bpmn);

        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * Temporary function until auto-save is implemented
     */
    @PostMapping(value = "/saveWorkflowFrontConfig/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveWorkflowFrontConfig(@PathVariable("projectId") String projectId,
                                                     @PathVariable("key") String key, @RequestBody String config) {
        try {
            projectService.getProject(projectId);

            workflowService.setFrontConfig(projectId, key, config);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, FRONT_CONFIG));
    }

    /*
     * Delete a list of workflows
     */
    @PostMapping(value = "/deleteWorkflows/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkflows(@PathVariable("projectId") String projectId,
                                             @RequestBody(required = false) List<String> ids) {
        try {
            projectService.getProject(projectId);

            workflowService.removeWorkflows(projectId, ids);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_DELETED_SUCCESSFULLY));
    }

    /*
     * Build a list of workflows
     */
    @PostMapping(value = "/buildWorkflows/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflows(@PathVariable String projectId,
                                            @RequestBody(required = false) List<String> ids) {

        try {
            projectService.getProject(projectId);

            List<OperationStatusDetails> results = workflowService.buildWorkflows(projectId, ids)
                    .get();
            return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, "Build completed.", results));
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

    }

    /*
     * Run Workflow
     */
    @PostMapping(value = "/runWorkflows/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflows(@PathVariable String projectId,
                                          @RequestBody(required = false) List<String> ids) {
        try {
            projectService.getProject(projectId);
            List<OperationStatusDetails> results = workflowService.runWorkflows(projectId, ids)
                    .get();
            return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, "Run completed.", results));
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

    }

    /*
     * Stop Workflow
     */
    @PostMapping(value = "/stopWorkflows/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopWorkflow(@PathVariable String projectId,
                                          @RequestBody(required = false) List<String> ids) {
        try {
            projectService.getProject(projectId);
            List<OperationStatusDetails> results = workflowService.stopWorkflows(projectId, ids)
                    .get();
            return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, "Stop completed.", results));
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(@ModelAttribute LibModel libModel) {

        try {
            workflowService.addAttachment(libModel);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_UPDATED));
    }

    @DeleteMapping(value = "/deleteAttachment/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable String id) {
        try {
            workflowService.deleteAttachmentByName(id);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_UPDATED));
    }


}
