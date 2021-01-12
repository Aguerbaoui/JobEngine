package io.je.project.controllers;


import io.je.project.beans.JEProject;
import io.je.project.models.ProjectModel;
import io.je.project.models.WorkflowModel;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
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
@RequestMapping(value= "/project")
public class ProjectController {




    @Autowired
    ProjectService projectService;

//########################################### **PROJECT** ################################################################

    /*
    * Add new project
    * */
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
    * */
    @PostMapping(value = "/buildProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildProject(@PathVariable String projectId) {
        try {
            projectService.buildAll(projectId);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        JELogger.info(ProjectController.class, "Built workflow successfully");
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BUILT_EVERYTHING_SUCCESSFULLY));
    }

    /*Run project*/
    @PostMapping(value = "/runProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runProject(@PathVariable String projectId) {
        try {
            projectService.runAll(projectId);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.NETWORK_ERROR, Errors.NETWORK_ERROR));
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, BUILT_EVERYTHING_SUCCESSFULLY1));
    }
 //########################################### **WORKFLOW** ################################################################
    /*
    * Add workflow to project
    * */
    @PostMapping(value = "/addWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkflow(@RequestBody WorkflowModel m) {
        JEWorkflow wf = new JEWorkflow();
        wf.setJobEngineElementID(m.getKey());
        wf.setJobEngineProjectID(m.getProjectId());
        wf.setWorkflowName(m.getName());
        try {
            projectService.addWorkflowToProject(wf);
        } catch (ProjectNotFoundException e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ADDED_WORKFLOW_SUCCESSFULLY));
    }

    /*
     * Build workflow
     * */
    @PostMapping(value = "/buildWorkflow", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildWorkflow(@RequestBody WorkflowModel m) {

        try {
            projectService.buildWorkflow(m.getProjectId(), m.getKey());
        } catch (ProjectNotFoundException e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (WorkflowNotFoundException e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowNotFound));
        }
        catch (IOException e) {
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.NETWORK_ERROR, Errors.NETWORK_ERROR));
        }
        catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }

        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, WORKFLOW_BUILT_SUCCESSFULLY));
    }

    /*
     * Run Workflow
     * */
    @PostMapping(value = "/runWorkflow/{projectId}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            projectService.runWorkflow(projectId, key);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.projectNotFound));
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), Errors.workflowNotFound));
        }
        catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(ResponseCodes.UNKNOWN_ERROR, Errors.uknownError));
        }
        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, EXECUTING_WORKFLOW));
    }

    /*
     * Delete a workflow
     */
    @DeleteMapping(value = "/deleteWorkflow/{projectId}/{workflowId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteWorkflow(@PathVariable("projectId") String projectId,@PathVariable("workflowId") String workflowId) {

        try {
            projectService.deleteWorkflowFromProject(projectId,workflowId);
            projectService.saveProject(ProjectService.getProjectById(projectId));
        } catch (ProjectNotFoundException | WorkflowNotFoundException e) {
            JELogger.error(ProjectController.class, e.getMessage());
            return ResponseEntity.badRequest().body(new JEResponse(e.getCode(), e.getMessage()));
        }


        return ResponseEntity.ok(new JEResponse(ResponseCodes.CODE_OK, ResponseMessages.WorkflowDeletionSucceeded));
    }

    @GetMapping(value="/getAllWorkflows/{projectId}")
    @ResponseBody
    public HashMap<String, JEWorkflow> getAllWorkflows(@PathVariable("projectId") String projectId) {
        return projectService.getAllWorkflows(projectId);
    }
}
