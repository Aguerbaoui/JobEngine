package io.je.runtime.controllers;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.ProjectAlreadyRunningException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RulesNotFiredException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.je.utilities.constants.ResponseMessages.RULE_BUILD_ERROR;


/*
 * Runtime manager project controller
 * */
@RestController
@RequestMapping(value= "/project")
public class ProjectController {

    @Autowired
    RuntimeDispatcher dispatcher;
    /*
     * Build whole project
     * */

    @PostMapping(value = "/buildProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildProject(@RequestBody String input) {
        try {
            dispatcher.buildProject(input);
        } catch (RuleBuildFailedException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.BUILT_EVERYTHING_SUCCESSFULLY));

    }

    /*
     * Run the whole project ( rules and workflows )
     * */
    @PostMapping(value = "/runProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runProject(@RequestBody String input) {
        //Start listening via data listener do not forget plz
        try {
            dispatcher.runProject(input);
        } catch (RulesNotFiredException | RuleBuildFailedException | ProjectAlreadyRunningException | WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EXECUTING_PROJECT));

    }

    /*
     * Stop the project
     * */
    @PostMapping(value = "/stopProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopProject(@RequestBody String input) {
        //Stop listening via data listener do not forget plz
        try {
            dispatcher.stopProject(input);
        } catch (RulesNotFiredException | RuleBuildFailedException | ProjectAlreadyRunningException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.STOPPING_PROJECT));

    }

    /*
     * Initialize the project
     * */
  /*  @RequestMapping(value = "/initProject", method = RequestMethod.GET)
    public ResponseEntity<?> initProject() {
        WorkflowEngineHandler.initWorkflowEngine();
        return new ResponseEntity<Object>(HttpStatus.OK).ok("Workflow Initialized");

    }*/


}
