package io.je.runtime.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.je.runtime.workflow.WorkflowEngineHandler;


/*
 * Runtime manager project controller
 * */
@RestController
public class ProjectController {
	
	/*
	 * Build whole project
	 * */
	
	@PostMapping(value = "/buildProject", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildProject(@RequestBody String input) {		
		return new ResponseEntity<Object>(HttpStatus.OK);
		
	}
	
	/*
	 * Run the whole project ( rules and workflows )
	 * */
	@PostMapping(value = "/runProject", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runProject(@RequestBody String input) {		
		return new ResponseEntity<Object>(HttpStatus.OK);
		
	}

	/*
	 * Stop the project
	 * */
	@PostMapping(value = "/stopProject", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> stopProject(@RequestBody String input) {		
		return new ResponseEntity<Object>(HttpStatus.OK);
		
	}
	
	/*
	 * Initialize the project
	 * */
	@RequestMapping(value = "/initProject", method = RequestMethod.GET)
	public ResponseEntity<?> initProject() {	
		WorkflowEngineHandler.initWorkflowEngine();
		return new ResponseEntity<Object>(HttpStatus.OK).ok("Workflow Initialized");
		
	}


}
