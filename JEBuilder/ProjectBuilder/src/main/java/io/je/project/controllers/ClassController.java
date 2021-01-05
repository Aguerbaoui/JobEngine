package io.je.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.je.classbuilder.models.ClassModel;
import io.je.project.services.ClassService;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.ClassFormatInvalidException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Response;

/*
 * Class Builder Rest Controller
 */

@RestController
@RequestMapping(value= "/class")
public class ClassController {
	@Autowired
	ClassService classService = new ClassService();
	
	
	/*
	 * add new class
	 */
	@PostMapping(value = "/{projectId}/addclass", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> addClass(@PathVariable("projectId") String projectId, @RequestBody ClassModel classModel)
	{
		try {
			classService.addClass(projectId, classModel);
		} catch (ProjectNotFoundException | ClassFormatInvalidException e) {
			e.printStackTrace();
			JELogger.error(ClassController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new Response(e.getCode(), e.getMessage()));
		}
		return ResponseEntity.ok(new Response(APIConstants.CODE_OK, ResponseMessages.RuleAdditionSucceeded));

	}

	
}
