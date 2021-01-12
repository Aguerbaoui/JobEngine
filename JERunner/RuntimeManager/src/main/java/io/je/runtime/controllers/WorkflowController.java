package io.je.runtime.controllers;

import io.je.runtime.models.WorkflowModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.WorkflowNotFoundException;
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
        dispatcher.buildWorkflow(wf.getKey());
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));

    }

    /*
     * Build and deploy workflow
     * */
    @PostMapping(value = "/buildWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@PathVariable String key) {
        dispatcher.buildWorkflow(key);
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_DEPLOYED));
    }

    /*
     * Run workflow
     * */
    @GetMapping(value = "/runWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String key) {
        try {
            //JELogger.info(WorkflowController.class, "Executing");
            dispatcher.launchProcessWithoutVariables(key);
        } catch (WorkflowNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));

    }

    /*
     * Run all available and deployed workflows
     * */
    @PostMapping(value = "/runAllWorkflows", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runAllWorkflows() {
        try {
            dispatcher.runAllWorkflows();
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }


}
