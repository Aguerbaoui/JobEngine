package io.je.project.controllers;

import io.je.project.beans.JEProject;
import io.je.project.exception.JEExceptionHandler;
import io.je.project.services.ConfigurationService;
import io.je.project.services.ProjectService;
import io.je.utilities.beans.InformModel;
import io.je.utilities.beans.JECustomResponse;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.log.JELogger;
import io.je.utilities.ruleutils.OperationStatusDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.log.LogCategory;
import utils.log.LogMessage;
import utils.log.LogSubModule;

import java.util.HashMap;
import java.util.List;

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

	@Autowired
	ConfigurationService configService;
//########################################### **PROJECT** ################################################################

	/*
	 * Get project running status
	 */
	@GetMapping("/getProjectRunStatus/{projectId}")
	public ResponseEntity<?> getProjectRunStatus(@PathVariable String projectId) {
		JEProject project = null;
		try {
			project = projectService.getProject(projectId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(project.isRunning());

	}
	
	/*
	 * Get project running status
	 */
	@GetMapping("/getProjectGlobalInfo/{projectId}")
	public ResponseEntity<?> getProjectGlobalInfo(@PathVariable String projectId) {
		JEProject project = null;
		HashMap<String,Integer> data = new HashMap<>();

		try {
			project = projectService.getProject(projectId);
			if(project!=null)
			{
				data.put("ruleCount",  project.getRules().size());
				data.put("workflowCount",  project.getWorkflows().size());
				data.put("eventCount",  project.getEvents().size());
			}
			else
			{
				return  JEExceptionHandler.handleException(new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND));
			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(data);
	}
	
	
	/*
	 * check if block name is unique
	 */
	@GetMapping("/getIsBlockNameUnique/{projectId}/{blockName}")
	public ResponseEntity<?> getIsBlockNameUnique(@PathVariable String projectId,@PathVariable String blockName) {
		JEProject project = null;
		try {
			project = projectService.getProject(projectId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(project==null?false:!project.blockNameExists(blockName));

	}
	

	/*
	 * Get project built status
	 */
	@GetMapping("/getProjectBuildStatus/{projectId}")
	public ResponseEntity<?> getProjectBuildStatus(@PathVariable String projectId) {
		JEProject project = null;
		try {
			project = projectService.getProject(projectId);

		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

		return ResponseEntity.ok(project.isBuilt());

	}
	/*
	 * Add new project
	 */
	@DeleteMapping(value = "/deleteProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteProject(@PathVariable String projectId) {
		if (!projectService.projectExists(projectId)) {
			return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_DELETED));
		}

		try {

			projectService.removeProject(projectId).get();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_DELETED));
	}


	/*
	 * Build entire project files
	 */
	@PostMapping(value = "/buildProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> buildProject(@PathVariable String projectId) {
		try {
			List<OperationStatusDetails> results = projectService.buildAll(projectId);
			return ResponseEntity.ok(new JECustomResponse(ResponseCodes.CODE_OK, BUILT_EVERYTHING_SUCCESSFULLY,results));
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

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

		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, PROJECT_STOPPED));
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
	 * Add new project
	 */
	@PostMapping(value = "/{projectId}/setProjectAutoReload", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setProjectAutoReload(@RequestBody boolean autoReload,@PathVariable String projectId) {
		if (!projectService.projectExists(projectId)) {
			return ResponseEntity.ok(new JEResponse(ResponseCodes.PROJECT_NOT_FOUND, JEMessages.PROJECT_NOT_FOUND));
		}
		try {
			JEProject project = projectService.getProject(projectId);
			if(project != null) {
				JELogger.debug("[project =" + project.getProjectName() + " ]  " + JEMessages.PROJECT_AUTO_RELOAD + autoReload, LogCategory.DESIGN_MODE,
						projectId, LogSubModule.JEBUILDER, null);
				project.setAutoReload(autoReload);
				projectService.saveProject(project).get();
			}
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.PROJECT_UPDATED));
	}

	@GetMapping(value = "/updateRunner", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateRunner() {

		try {
			configService.updateRunner();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, "Updated"));
	}

	@GetMapping(value = "/cleanUp", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> cleanUpHouse() {
		try {
			projectService.cleanUpHouse();
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}
		return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, "Updated"));
	}
	/*
	 * Inform message from workflow in runtime
	 */
	@PostMapping(value = "/informUser", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> informUser(@RequestBody InformModel informBody) {
		try {
			projectService.informUser(informBody);
			return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.SUCCESSFULLY_INFORMED));
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

	}

	/*
	 * send log message from workflow in runtime
	 */
	@PostMapping(value = "/sendLog", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> informUser(@RequestBody LogMessage logMessage) {
		try {
			projectService.sendLog(logMessage);
			return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.SUCCESSFULLY_INFORMED));
		} catch (Exception e) {
			return JEExceptionHandler.handleException(e);
		}

	}

}
