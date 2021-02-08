package io.je.project.controllers;

import io.je.project.beans.JEProject;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.models.ProjectModel;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.ResponseMessages.*;

/*
 * Project Rest Controller
 * */
@RestController
@RequestMapping(value = "/project")
@CrossOrigin(maxAge = 3600)
public class ProjectController {

	@Autowired
	ProjectService projectService;

//########################################### **PROJECT** ################################################################
	
	@GetMapping("/getAllProjects")
	public ResponseEntity<?> getAllProject(@PathVariable String projectId) {
		Collection<?> projects = null;
		try {
			projects = projectService.getAllProjects().get();
			 if(projects.isEmpty())
			 {
					return ResponseEntity.noContent().build();

			 }
		} catch (Exception e) {
			e.printStackTrace();
			JELogger.error(RuleController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
 		}
		
		return	new ResponseEntity<Object>(projects,HttpStatus.OK);
	
}

	
	

	/*
	 * Add new project
	 */
	@PostMapping(value = "/addProject", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addProject(@RequestBody ProjectModel m) {
		
		//TODO: add control if project exists
		
		try {
			JEProject p = new JEProject(m.getProjectId(), m.getProjectName(), m.getConfigurationPath());
			JELogger.trace(ProjectController.class, "Creating project with id = " + m.getProjectId());
			projectService.saveProject(p).get();
		}catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, CREATED_PROJECT_SUCCESSFULLY));
	}
	
	/*
	 * Add new project
	 */
	@PostMapping(value = "/deleteProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
		
		//TODO: control if project exists
		
		try {
			projectService.removeProject(projectId).get();
		}catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, CREATED_PROJECT_SUCCESSFULLY));
	}

	@GetMapping("/getProject/{projectId}")
	public ResponseEntity<?> getProject(@PathVariable String projectId) {
	JEProject project=null;
	try {
		project = projectService.getProject(projectId).get();
	} catch (Exception e) {
		return JEExceptionHandler.handleException(e);

	}
	if(project==null) {
		return ResponseEntity.ok(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, Errors.PROJECT_NOT_FOUND));

	}
	
	return ResponseEntity.ok(project);

	}

	/*
	 * Build entire project files
	 */
	@PostMapping(value = "/buildProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildProject(@PathVariable String projectId) {
		try {
			projectService.buildAll(projectId);


		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		JELogger.trace(ProjectController.class, BUILT_EVERYTHING_SUCCESSFULLY);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BUILT_EVERYTHING_SUCCESSFULLY));
	}

	/* Run project */
	@PostMapping(value = "/runProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runProject(@PathVariable String projectId) {
		try {
			
				projectService.runAll(projectId);
				

			
		}catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_RUNNING));
	}
	
	
	/* Stop project */
	@PostMapping(value = "/stopProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> stopProject(@PathVariable String projectId) {
		
			
				try {
					projectService.stopProject(projectId).get();

				} catch (Exception e) {
					return JEExceptionHandler.handleException(e);

				}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, STOPPING_PROJECT));
	}

}
