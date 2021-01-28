package io.je.runtime.controllers;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.je.utilities.constants.ResponseMessages.*;

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
        //JELogger.info(WorkflowController.class, wf.toString());
        dispatcher.addWorkflow(wf);
        dispatcher.buildWorkflow(wf.getProjectId(), wf.getKey());
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));

    }

    /*
     * Build and deploy workflow
     * */
    @PostMapping(value = "/buildWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@PathVariable String projectId, @PathVariable String key) {
        dispatcher.buildWorkflow(projectId, key);
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_DEPLOYED));
    }

    /*
     * Run workflow
     * */
    @GetMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            //JELogger.info(WorkflowController.class, "Executing");
            dispatcher.launchProcessWithoutVariables(projectId, key);
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
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
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }


}
