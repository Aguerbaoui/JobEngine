package io.je.runtime.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.je.runtime.models.ClassModel;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.network.JEResponse;


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
			} catch (ClassLoadException e) {
				e.printStackTrace();
				return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
			}
		
			
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.CLASS_WAS_ADDED_SUCCESSFULLY));
	}
	
	
		
		
		
		
			
		
		
}
