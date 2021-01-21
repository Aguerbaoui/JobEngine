package io.je.project.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.je.project.services.ClassService;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.logger.JELogger;
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
	 */
	@PostMapping(value = "/addclass/{worksapceId}/{classId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JEResponse> addClass(@PathVariable("worksapceId") String worksapceId, @PathVariable("classId") String classId)
	{
		try {
		
			classService.addClass(worksapceId, classId);
		
		} catch ( ClassLoadException | AddClassException | DataDefinitionUnreachableException | JERunnerUnreachableException e) {
			e.printStackTrace();
			JELogger.error(ClassController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
		} catch (IOException e) {
			
			e.printStackTrace();
			JELogger.info(WorkflowController.class, Errors.uknownError);
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.classAddedSuccessully));

	}
	
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
