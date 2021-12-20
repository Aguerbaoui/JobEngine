package io.je.runtime.controllers;


import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.models.ClassModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;


/*
 * Class Controller Class
 */

@RestController
@CrossOrigin(maxAge = 3600)
public class ClassController {
	
	
	@Autowired
	RuntimeDispatcher runtimeDispatcher;


	/*
	 * add a new class
	 */
	@PostMapping(value = "/addClass", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addClass( @RequestBody ClassModel classModel) {
		
	
			try {
				runtimeDispatcher.addClass(classModel);
			} catch (Exception e) {
				return JEExceptionHandler.handleException(e);
			}
		
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.CLASS_WAS_ADDED_SUCCESSFULLY));
	}
	
	/*
	 * update class
	 */
	@PostMapping(value = "/updateClass", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateClass( @RequestBody ClassModel classModel, HttpServletRequest request) {
		
	
			try {

				synchronized (runtimeDispatcher) {
					runtimeDispatcher.updateClass(classModel);	
				}
			} catch (Exception e) {
				return JEExceptionHandler.handleException(e);
			}
		
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.CLASS_WAS_ADDED_SUCCESSFULLY));
	}

	/*
	
	/*
	 * Adding a list of classes
	 */
	/*@PostMapping(value = "/addClasses", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addClasses( @RequestBody List<ClassModel> classModelList) {


		try {
			runtimeDispatcher.updateClasses(classModelList);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}



		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.CLASS_WAS_ADDED_SUCCESSFULLY));
	}*/

	@PostMapping("/uploadJar")
	public ResponseEntity<?> uploadJar(@RequestBody HashMap<String, String> payload) {

		try {
			runtimeDispatcher.addJarToProject(payload);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_UPDATED));
	}
	
	
		
		
		
		
			
		
		
}
