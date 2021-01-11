package io.je.runtime.controllers;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.JEFileNotFoundException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleFormatNotValidException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;


/*
 * Rule Controller Class
 */

@RestController
public class RuleController {
	
	
	@Autowired
	RuntimeDispatcher runtimeDispatcher = new RuntimeDispatcher();

	/*
	 * add a new Rule
	 */
	@PostMapping(value = "/addRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addRule( @RequestBody RuleModel ruleModel) {
		
		try {
			runtimeDispatcher.addRule(ruleModel);
		} catch (RuleAlreadyExistsException | JEFileNotFoundException |RuleFormatNotValidException| RuleNotAddedException e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		
		} catch (RuleCompilationException e) {
			
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));
		
		} 
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));
	}
	
	/*
	 * update a  Rule
	 */
	@PostMapping(value = "/updateRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateRule( @RequestBody RuleModel ruleModel) {
		
		try {
			runtimeDispatcher.updateRule(ruleModel);
		} catch (  JEFileNotFoundException |RuleFormatNotValidException e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		
		} catch (RuleCompilationException e) {
			
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));
		
		} 
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
	}


	
	/*
	 * compile  a  Rule
	 */
	@PostMapping(value = "/compileRule", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> compileRule( @RequestBody RuleModel ruleModel) {
		
		try {
			runtimeDispatcher.compileRule(ruleModel);
		} catch (  JEFileNotFoundException |RuleFormatNotValidException e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		
		} catch (RuleCompilationException e) {
			
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getCompilationError()));
		
		} 
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.RuleUpdateSucceeded));
	}

	
	//TODO: move method to class controller
	/*
	 * add a new class
	 */
	@PostMapping(value = "/addClass", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addClass( @RequestBody ClassModel classModel) {
		
	
			try {
				runtimeDispatcher.addClass(classModel.getClassPath());
			} catch (ClassLoadException e) {
				e.printStackTrace();
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.classAddedSuccessully));
	}
	
	
		@PostMapping(value = "/setLoadPath", produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<?> setLoadPath( @RequestBody String classPath) {
			
		
				runtimeDispatcher.setClassLoadPath(classPath);
				
			
			return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.classAddedSuccessully));
		}
}
