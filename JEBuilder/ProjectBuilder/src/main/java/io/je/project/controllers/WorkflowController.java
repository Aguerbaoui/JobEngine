package io.je.project.controllers;

import io.je.project.exception.JEExceptionHandler;
import io.je.project.models.WorkflowBlockModel;
import io.je.project.services.ProjectService;
import io.je.project.services.WorkflowService;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.network.JEResponse;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static io.je.utilities.constants.JEMessages.*;

/*
 * Workflow builder Rest Controller
 * */
@RestController
@RequestMapping(value = "/workflow")
@CrossOrigin(maxAge = 3600)
public class WorkflowController {


    @Autowired
    WorkflowService workflowService;

    @Autowired
    ProjectService projectService;

    @PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel m) {
        try {

            workflowService.addWorkflow(m);
            projectService.saveProject(m.getProjectId());
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * Build workflow
     */
    @PostMapping(value = "/buildWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@PathVariable String projectId, @PathVariable String key) {

        try {
            workflowService.buildWorkflow(projectId, key);
        }catch (Exception e) {
			return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_BUILT_SUCCESSFULLY));
    }

    /*
     * Run Workflow
     */
    @PostMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            workflowService.runWorkflow(projectId, key);
            projectService.saveProject(projectId);
        }catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }

    /*
     * Stop Workflow
     */
    @PostMapping(value = "/stopWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            workflowService.stopWorkflow(projectId, key);
            projectService.saveProject(projectId);
        }catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }

    /*
     * Delete a workflow
     */
    @DeleteMapping(value = "/deleteWorkflow/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("workflowId") String workflowId) {

        try {
            workflowService.removeWorkflow(projectId, workflowId);
            projectService.saveProject(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.WORKFLOW_DELETED_SUCCESSFULLY));
    }

    /*
     * Delete a workflow
     */
    @PatchMapping(value = "/updateWorkflow/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWorkflow(@PathVariable("projectId") String projectId,
                                            @PathVariable("workflowId") String workflowId, @RequestBody WorkflowModel m) {

        try {
            workflowService.updateWorkflow(projectId, workflowId, m);
            projectService.saveProject(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    @GetMapping(value = "/getAllWorkflows/{projectId}")
    @ResponseBody
    public ResponseEntity<?> getAllWorkflows(@PathVariable("projectId") String projectId) {
        try {
            return ResponseEntity.ok(projectService.getAllWorkflows(projectId));
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
    }

    @GetMapping(value = "/getWorkflowById/{projectId}/{key}")
    @ResponseBody
    public ResponseEntity<?> getWorkflowById(@PathVariable("projectId") String projectId, @PathVariable("key") String key) {
        JEWorkflow w = null;
        try {
            w = projectService.getWorkflowById(projectId, key);
        }catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
        if (w != null) {
            return ResponseEntity.ok(w);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.WORKFLOW_NOT_FOUND, JEMessages.WORKFLOW_NOT_FOUND));
    }

    /*
     * Add a new Workflow component
     */
    @PostMapping(value = "/addWorkflowBlock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflowBlock(@RequestBody WorkflowBlockModel block) {
    	String generatedBlockName = "";

    	try {

        	generatedBlockName=  workflowService.addWorkflowBlock(block);
            projectService.saveProject(block.getProjectId());

        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
    	HashMap<String, String> object = new HashMap<>();

		object.put("blockName", generatedBlockName);
		return ResponseEntity.ok(object);
    }

    @PatchMapping(value = "/updateWorkflowBlock")
    public ResponseEntity<?> updateWorkflowBlock(@RequestBody WorkflowBlockModel block) {

        try {
            workflowService.updateWorkflowBlock(block);
            projectService.saveProject(block.getProjectId());
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_UPDATED_SUCCESS));
    }

    /*
     * Delete a wokflow block
     * */
    @DeleteMapping(value = "deleteWorkflowBlock/{projectId}/{key}/{id}")
    public ResponseEntity<?> deleteWorkflowBlock(@PathVariable String projectId, @PathVariable String key, @PathVariable String id) {

        try {
            workflowService.deleteWorkflowBlock(projectId, key, id);
            projectService.saveProject(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BLOCK_DELETED_SUCCESSFULLY));
    }

    /*
     * Delete a sequence flow in a workflow
     * */
    @DeleteMapping(value = "deleteSequenceFlow/{projectId}/{key}/{from}/{to}")
    public ResponseEntity<?> deleteSequenceFlow(@PathVariable String projectId, @PathVariable String key, @PathVariable String from, @PathVariable String to) {

        try {
            workflowService.deleteSequenceFlow(projectId, key, from, to);
            projectService.saveProject(projectId);
        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, SEQUENCE_FLOW_DELETED_SUCCESSFULLY));

    }

    /*
     * add a new scripted Rule
     */
    @PostMapping(value = "/addBpmn/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addScriptedRule(@PathVariable("projectId") String projectId, @PathVariable("workflowId") String workflowId, @RequestBody String bpmn) {

        try {
            workflowService.addBpmn(projectId, workflowId, bpmn);
            projectService.saveProject(projectId);

        } catch (Exception e) {
			return JEExceptionHandler.handleException(e);

		}

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, JEMessages.ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * temporary function until autosave is implemented
     */
    @PostMapping(value = "/saveWorkflowFrontConfig/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveWorkflowFrontConfig(@PathVariable("projectId") String projectId,@PathVariable("workflowId") String workflowId, @RequestBody String config) {
        try {
            workflowService.setFrontConfig(projectId, workflowId, config);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);

        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, FRONT_CONFIG));
    }

    @DeleteMapping(value = "/deleteWorkflows/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteRules(@PathVariable("projectId") String projectId, @RequestBody List<String> ids) {
        try {
            workflowService.removeWorkflows(projectId, ids);
        } catch (Exception e) {
            return JEExceptionHandler.handleException(e);
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_DELETED_SUCCESSFULLY));
    }

}
