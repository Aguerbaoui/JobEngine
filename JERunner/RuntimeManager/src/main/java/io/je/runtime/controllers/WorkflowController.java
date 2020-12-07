package io.je.runtime.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import builder.JEToBpmnMapper;
import io.je.runtime.models.WorkflowModel;
import io.je.runtime.workflow.WorkflowEngineHandler;

/*
 * Workflow Rest Controller
 * */
@RestController
public class WorkflowController {

	
	/*
	 * Add a new Workflow
	 * */
	@PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel wf) {		
		return new ResponseEntity<String>(HttpStatus.OK);
		
	}
	
	/*
	 * Build and deploy workflow
	 * */
	@PostMapping(value = "/buildWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildWorkflow(@PathVariable String key) {	
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	/*
	 * Run workflow
	 * */
	@PostMapping(value = "/runWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runWorkflow(@PathVariable String key) {	
		WorkflowEngineHandler.launchProcessWithoutVariables(key);
		return new ResponseEntity<String>(HttpStatus.OK).ok("");
		
	}
	
	/*
	 * Run all available and deployed workflows
	 * */
	@PostMapping(value = "/runAllWorkflows", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runAllWorkflows() {	
		return new ResponseEntity<String>(HttpStatus.OK).ok("");
	}
	

}
