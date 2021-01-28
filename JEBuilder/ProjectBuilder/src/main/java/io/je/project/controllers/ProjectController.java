package io.je.project.controllers;

import io.je.project.beans.JEProject;
import io.je.project.models.ProjectModel;
import io.je.project.models.WorkflowModel;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

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
		projectService.saveProject(p);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, CREATED_PROJECT_SUCCESSFULLY));
	}

	@GetMapping("/getProject/{projectId}")
	public JEProject getProject(@PathVariable String projectId) {
		return projectService.getProject(projectId);
	}

	/*
	 * Build entire project files
	 */
	@PostMapping(value = "/buildProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildProject(@PathVariable String projectId) {
		try {
			projectService.buildAll(projectId);
		} catch (ProjectNotFoundException | WorkflowNotFoundException | RuleBuildFailedException
				| JERunnerErrorException | DataDefinitionUnreachableException | AddClassException | ClassLoadException  e) {
			return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));

		} catch (Exception e) {
			e.printStackTrace();
			JELogger.info(ProjectController.class, e.getMessage());
			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
		}
		JELogger.info(ProjectController.class, BUILT_EVERYTHING_SUCCESSFULLY);
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BUILT_EVERYTHING_SUCCESSFULLY));
	}

	/* Run project */
	@PostMapping(value = "/runProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> runProject(@PathVariable String projectId) {
		try {
			try {
				projectService.runAll(projectId);
			} catch (JERunnerErrorException | ProjectRunException | ProjectNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.NETWORK_ERROR, Errors.NETWORK_ERROR));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_RUNNING));
	}
	
	
	/* Stop project */
	@PostMapping(value = "/stopProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> stopProject(@PathVariable String projectId) {
		
			
				try {
					projectService.stopProject(projectId);
				} catch (ProjectNotFoundException | JERunnerErrorException | ProjectRunException
						e) {
					e.printStackTrace();
					JELogger.error(RuleController.class, e.getMessage());
					return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
				} catch (Exception e) {
					return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));

				}
		
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, STOPPING_PROJECT));
	}

}
