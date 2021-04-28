package io.je.runtime.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.exception.JEExceptionHandler;
import io.je.runtime.models.ClassModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.network.JEResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;


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
