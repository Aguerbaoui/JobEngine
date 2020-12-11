package io.je.runtime.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import builder.JEToBpmnMapper;
import io.je.runtime.models.WorkflowModel;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;

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
		JELogger.info(wf.toString());
		WorkflowEngineHandler.addProcess(wf.getKey(), wf.getPath());
		return ResponseEntity.ok("Deploying workflow to engine");
		
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
	@GetMapping(value = "/runWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runWorkflow(@PathVariable String key) {	
		try {
			JELogger.info("Executing");
			WorkflowEngineHandler.launchProcessWithoutVariables(key);
		} catch (WorkflowNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.ok(Errors.workflowNotFound);
		}
		return ResponseEntity.ok("Executing workflow");
		
	}
	
	/*
	 * Run all available and deployed workflows
	 * */
	@PostMapping(value = "/runAllWorkflows", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runAllWorkflows() {	
		new ResponseEntity<String>(HttpStatus.OK);
		return ResponseEntity.ok("");
	}
	

}
