package io.je.project.controllers;


import io.je.project.beans.JEProject;
import io.je.project.models.ProjectModel;
import io.je.project.models.WorkflowModel;
import io.je.project.services.ProjectService;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Response;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/*
 * Project Rest Controller
 * */
@RestController("project")
public class ProjectController {


    @Autowired
    ProjectService projectService;
//########################################### **PROJECT** ################################################################

    /*
    * Add new project
    * */
    @PostMapping(value = "/addProject", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProject(@RequestBody ProjectModel m) {
        JEProject p = new JEProject(m.getProjectId(), m.getProjectName());
        projectService.saveProject(p);
        return ResponseEntity.ok(new Response(200, "Created project successfully"));
    }

    /*
    * Build entire project files
    * */
    @PostMapping(value = "/buildProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buildProject(@PathVariable String projectId) {
        try {
            projectService.buildAll(projectId);
        } catch (ProjectNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.ok(Errors.projectNotFound);
        } catch (Exception e) {
            return ResponseEntity.ok(Errors.uknownError);
        }
        return ResponseEntity.ok(new Response(200, "Built everything successfully"));
    }

    /*Run project*/
    @PostMapping(value = "/runProject/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runProject(@PathVariable String projectId) {
        try {
            projectService.runAll(projectId);
        } catch (IOException e) {
            return ResponseEntity.ok(Errors.uknownError);
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.ok(Errors.projectNotFound);
        }
        return ResponseEntity.ok(new Response(200, "Built everything successfully"));
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
            return ResponseEntity.ok(Errors.projectNotFound);
        } catch (Exception e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.ok(Errors.uknownError);
        }
        JELogger.info(WorkflowController.class, "Added workflow successfully");
        return ResponseEntity.ok(new Response(200, "Added workflow successfully"));
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
            return ResponseEntity.ok(Errors.projectNotFound);
        } catch (WorkflowNotFoundException e) {
            JELogger.info(WorkflowController.class, e.getMessage());
            return ResponseEntity.ok(Errors.workflowNotFound);
        }
        return ResponseEntity.ok("Workflow built successfully");
    }

    /*
     * Run Workflow
     * */
    @PostMapping(value = "/runWorkflow/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> runWorkflow(@PathVariable String projectId, @PathVariable String key) {
        try {
            JELogger.info(WorkflowController.class, key);
            projectService.runWorkflow(projectId, key);
        } catch (IOException e) {
            return ResponseEntity.ok("Error executing workflow");
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.ok(Errors.projectNotFound);
        } catch (WorkflowNotFoundException e) {
            return ResponseEntity.ok(Errors.workflowNotFound);
        }
        return ResponseEntity.ok("Executing workflow");
    }
}
