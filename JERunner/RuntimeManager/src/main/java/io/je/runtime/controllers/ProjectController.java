package io.je.runtime.controllers;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.ProjectAlreadyRunningException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RulesNotFiredException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
 * Runtime manager project controller
 * */
@RestController
@RequestMapping(value = "/project")
@CrossOrigin(maxAge = 3600)
public class ProjectController {

    @Autowired
    RuntimeDispatcher dispatcher;
    /*
     * Build whole project
     * */

    @GetMapping(value = "/buildProject", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping(value = "/runProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runProject(@PathVariable String projectId) {
        try {
            dispatcher.runProject(projectId);
        } catch (RulesNotFiredException | RuleBuildFailedException | ProjectAlreadyRunningException | WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.EXECUTING_PROJECT));

    }

    /*
     * Stop the project
     * */
    @GetMapping(value = "/stopProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopProject(@PathVariable String projectId) {
    	//TODO: add failed to stop project exception
            dispatcher.stopProject(projectId);

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.STOPPING_PROJECT));

    }

    /*
     * Add topics
     * */
    @PostMapping(value = "/addTopics/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addTopics(@PathVariable String projectId, @RequestBody List<String> topics) {
        //Stop listening via data listener do not forget plz
        dispatcher.addTopics(projectId, topics);
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.TOPIC_ADDED));
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
