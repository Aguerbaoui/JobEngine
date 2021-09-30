package io.je.runtime.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.WorkflowBuildException;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.beans.JEResponse;

import static io.je.utilities.constants.JEMessages.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Workflow Rest Controller
 * */
@RestController
@RequestMapping(value= "/workflow")
@CrossOrigin(maxAge = 3600)
public class WorkflowController {

    @Autowired
    RuntimeDispatcher dispatcher;

    /*
     * Add a new Workflow
     * */
    @PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel wf) {
        dispatcher.addWorkflow(wf);
        try {
            dispatcher.buildWorkflow(wf.getProjectId(), wf.getId());
        } catch (WorkflowBuildException e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));

    }

    /*
     * Build and deploy workflow
     * */
    @PostMapping(value = "/buildWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            dispatcher.buildWorkflow(projectId, key);
        } catch (WorkflowBuildException e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_DEPLOYED));
    }

    /*
     * Run workflow
     * */
    @GetMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            dispatcher.launchProcessWithoutVariables(projectId, key, false);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));

    }

    /*
     * Run all available and deployed workflows
     * */
    @PostMapping(value = "/runAllWorkflows/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runAllWorkflows(@PathVariable String projectId) {
        try {
            dispatcher.runAllWorkflows(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }
    /*
     * Delete a workflow
     */
    @DeleteMapping(value = "/deleteWorkflow/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("workflowId") String workflowId) {
        dispatcher.removeWorkflow(projectId, workflowId);
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.WORKFLOW_DELETED_SUCCESSFULLY));
    }

}
