package io.je.runtime.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.log.JELogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

  /*  @GetMapping(value = "/buildProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildProject(@RequestBody String input) {
        try {
            dispatcher.buildProject(input);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.BUILT_EVERYTHING_SUCCESSFULLY));

    }*/

    /*
     * Run the whole project ( rules and workflows )
     * */
    @GetMapping(value = "/runProject/{projectId}/{projectName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runProject(@PathVariable String projectId,@PathVariable String projectName) {
        try {
            dispatcher.runProject(projectId,projectName);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.EXECUTING_PROJECT));

    }

    /*
     * Stop the project
     * */
    @GetMapping(value = "/stopProject/{projectId}/{projectName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopProject(@PathVariable String projectId,@PathVariable String projectName) {
    	//TODO: add failed to stop project exception
            dispatcher.stopProject(projectId,projectName);

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_STOPPED));

    }

    /*
     * Delete whole project data ( rules and workflows and events)
     * */
    @GetMapping(value = "/removeProjectData/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeProjectData(@PathVariable String projectId) {
        try {
            dispatcher.removeProjectData(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_DELETED));

    }
    /*
     * Get app log
     * */
    @GetMapping(value = "/getLog", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLog() {
        //TODO: add failed to stop project exception
       // List l = new ArrayList(JELogger.getQueue());
        //JELogger.getQueue().removeAll(JELogger.getQueue());
        return ResponseEntity.ok(JELogger.getQueue());

    }
}
