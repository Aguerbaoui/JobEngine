package io.je.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ClassService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.network.JEResponse;

/*
 * Class Builder Rest Controller
 */

@RestController
@RequestMapping(value= "/class")
@CrossOrigin(maxAge = 3600)
public class ClassController {
	@Autowired
	ClassService classService ;
	
	
	/*
	 * add new class 
	 * TODO: 
	 */
/*	@PostMapping(value = "/addclass/{worksapceId}/{classId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addClass(@PathVariable("worksapceId") String worksapceId, @PathVariable("classId") String classId)
	{
		try {
		
			classService.addClass(worksapceId, classId);
		
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.CLASS_WAS_ADDED_SUCCESSFULLY));

	} */
	
	/*@PostMapping(value = "/{projectId}/addclass", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> addClass(@PathVariable("projectId") String projectId, @RequestBody ClassModel classModel)
	{
		try {
			classService.addClass(projectId, classModel);
		} catch (ProjectNotFoundException | ClassFormatInvalidException e) {
			e.printStackTrace();
			JELogger.error(ClassController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new Response(e.getCode(), e.getMessage()));
		}
		return ResponseEntity.ok(new Response(ResponseCodes.CODE_OK, ResponseMessages.RuleAdditionSucceeded));

	}*/

	
}
