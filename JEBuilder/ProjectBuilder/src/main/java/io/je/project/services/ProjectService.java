package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
/*
* Service class to handle business logic for projects
* */

@Service
public class ProjectService {

    @Autowired
    WorkflowService workflowService;
    
 

    // TODO add repo jpa save later
    private static HashMap<String, JEProject> loadedProjects = new HashMap<String, JEProject>();

    /*
    * Add a new project
    * */
    public void saveProject(JEProject project) {
        //Todo add repo jpa save operation
        loadedProjects.put(project.getProjectId(), project);
    }

    /*
    * Most likely won't be used
    * */
    public void removeProject(String id) throws ProjectNotFoundException{
        //TODO remove project from jpa db

        if(!loadedProjects.containsKey(id))
        {
            throw new ProjectNotFoundException("2", Errors.projectNotFound);
        }
        loadedProjects.remove(id);
    }

    /*
    *  Add a workflow to project
    * */
    public void addWorkflowToProject(JEWorkflow wf) throws ProjectNotFoundException {
        workflowService.addWorkflow(wf);
    }

    /*
    * Remove workflow from project
    * */
    public void deleteWorkflowFromProject(String projectId, String workflowId) throws ProjectNotFoundException, WorkflowNotFoundException {
        workflowService.removeWorkflow(projectId, workflowId);
    }

    /*
    * Add a rule to project
    * */
    public void addRuleToProject(UserDefinedRule rule) throws RuleAlreadyExistsException {
        loadedProjects.get(rule.getJobEngineProjectID()).addRule(rule);
    }

    public static HashMap<String, JEProject> getLoadedProjects() {
        return loadedProjects;
    }

    public static JEProject getProjectById(String id) {
        return loadedProjects.get(id);

    }
    public static void setLoadedProjects(HashMap<String, JEProject> loadedProjects) {
        ProjectService.loadedProjects = loadedProjects;
    }

    public void buildWorkflow(String projectId, String workflowId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException {
        workflowService.buildWorkflow(projectId, workflowId);
    }

    public void runWorkflow(String projectId, String workflowId) throws ProjectNotFoundException, IOException, WorkflowNotFoundException {
        workflowService.runWorkflow(projectId, workflowId);
    }

    public void buildAll(String projectId) throws WorkflowNotFoundException, ProjectNotFoundException, IOException {
        //TODO add build all rules
        workflowService.buildWorkflows(projectId);
    }

    public void runAll(String projectId) throws IOException, ProjectNotFoundException {
        //TODO add run all rules
        workflowService.runWorkflows(projectId);
    }
}
