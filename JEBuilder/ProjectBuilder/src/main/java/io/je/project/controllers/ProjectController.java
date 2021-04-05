package io.je.project.controllers;

import io.je.project.beans.JEProject;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.models.ProjectModel;
import io.je.project.services.ConfigurationService;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static io.je.utilities.constants.JEMessages.*;

/*
 * Project Rest Controller
 * */
@RestController
@RequestMapping(value = "/jeproject")
@CrossOrigin(maxAge = 3600)
public class ProjectController {

	@Autowired
	ProjectService projectService;

//########################################### **PROJECT** ################################################################
	/*
	 * Get the list of all projects
	 */
	@GetMapping("/getAllProjects")
	public ResponseEntity<?> getAllProjects() {
		Collection<?> projects = null;
		try {
			projects = projectService.getAllProjects().get();
			if (projects.isEmpty()) {
				return ResponseEntity.noContent().build();

			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(projects);

	}

	/*
	 * Get project running status
	 */
	@GetMapping("/getProjectRunStatus/{projectId}")
	public ResponseEntity<?> getProjectRunStatus(@PathVariable String projectId) {
		JEProject project = null;
		try {
			project = projectService.getProject(projectId).get();

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(project.isRunning());

	}

	/*
	 * Get project built status
	 */
	@GetMapping("/getProjectBuildStatus/{projectId}")
	public ResponseEntity<?> getProjectBuildStatus(@PathVariable String projectId) {
		JEProject project = null;
		try {
			project = projectService.getProject(projectId).get();

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(project.isBuilt());

	}

	/*
	 * Add new project
	 */
	@PostMapping(value = "/addProject", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addProject(@RequestBody ProjectModel m) {
		if (projectService.projectExists(m.getProjectId())) {
			return ResponseEntity.ok(new JEResponse(ResponseCodes.PROJECT_EXISTS, JEMessages.PROJECT_EXISTS));
		}
		try {
			JEProject p = new JEProject(m.getProjectId(), m.getConfigurationPath());
			projectService.saveProject(p).get();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, CREATED_PROJECT_SUCCESSFULLY));
	}

	/*
	 * Add new project
	 */
	@DeleteMapping(value = "/deleteProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
		if (!projectService.projectExists(projectId)) {
			return ResponseEntity.badRequest()
					.body(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, JEMessages.PROJECT_NOT_FOUND));
		}

		try {

			projectService.removeProject(projectId).get();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, CREATED_PROJECT_SUCCESSFULLY));
	}

	@GetMapping("/getProject/{projectId}")
	public ResponseEntity<?> getProject(@PathVariable String projectId) {
		JEProject project = null;
		try {
			project = projectService.getProject(projectId).get();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		if (project == null) {
			return ResponseEntity.ok(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, JEMessages.PROJECT_NOT_FOUND));

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

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_RUNNING));
	}

	/* Stop project */
	@PostMapping(value = "/stopProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> stopProject(@PathVariable String projectId) {

		try {
			projectService.stopProject(projectId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_STOPPEd));
	}

	/*
	 * Stop the project
	 */
	@GetMapping(value = "/getLog", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLog() {
		// TODO: add failed to stop project exception

		// List l = new ArrayList(JELogger.getQueue());
		// JELogger.getQueue().removeAll(JELogger.getQueue());
		return ResponseEntity.ok(JELogger.getQueue());

	}

	/*
	 * remove project from builder and runner
	 */
	@GetMapping(value = "/closeProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> closeProject(@PathVariable String projectId) {
		try {

			projectService.closeProject(projectId);
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_CLOSED));

	}

}
