package io.je.project.controllers;

import io.je.project.beans.JEProject;
import io.je.project.models.ProjectModel;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;

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

	/*
	 * Add new project
	 */
	@PostMapping(value = "/addProject", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addProject(@RequestBody ProjectModel m) {
		JEProject p = new JEProject(m.getProjectId(), m.getProjectName(), m.getConfigurationPath());
		JELogger.trace(ProjectController.class, "Creating project with id = " + m.getProjectId());
		projectService.saveProject(p);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, CREATED_PROJECT_SUCCESSFULLY));
	}

	@GetMapping("/getProject/{projectId}")
	public ResponseEntity<?> getProject(@PathVariable String projectId) {
		return ResponseEntity.ok(projectService.getProject(projectId) != null ?
				projectService.getProject(projectId) :
				new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, Errors.PROJECT_NOT_FOUND));
	}

	/*
	 * Build entire project files
	 */
	@PostMapping(value = "/buildProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildProject(@PathVariable String projectId) {
		try {
			projectService.buildAll(projectId);
		} catch (ProjectNotFoundException |  RuleBuildFailedException
				| JERunnerErrorException e) {
			JELogger.error(ProjectController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

		} catch (Exception e) {
			e.printStackTrace();
			JELogger.error(ProjectController.class, Arrays.toString(e.getStackTrace()));
			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
		}
		JELogger.trace(ProjectController.class, BUILT_EVERYTHING_SUCCESSFULLY);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BUILT_EVERYTHING_SUCCESSFULLY));
	}

	/* Run project */
	@PostMapping(value = "/runProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runProject(@PathVariable String projectId) {
		try {
			try {
				projectService.runAll(projectId);
			} catch (JERunnerErrorException | ProjectRunException | ProjectNotFoundException e) {
			}
		}
		catch (Exception e) {
			JELogger.error(ProjectController.class, Arrays.toString(e.getStackTrace()));
			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_RUNNING));
	}
	
	
	/* Stop project */
	@PostMapping(value = "/stopProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> stopProject(@PathVariable String projectId) {
		
			
				try {
					projectService.stopProject(projectId);
				} catch (ProjectNotFoundException | JERunnerErrorException | ProjectRunException | ProjectStatusException
						e) {
					JELogger.error(RuleController.class, e.getMessage());
					return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
				} catch (Exception e) {
					JELogger.error(ProjectController.class, Arrays.toString(e.getStackTrace()));
					return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.UKNOWN_ERROR));

				}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, STOPPING_PROJECT));
	}

}
