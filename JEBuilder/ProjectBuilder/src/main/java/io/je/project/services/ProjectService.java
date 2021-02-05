package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.repository.ProjectRepository;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
/*
 * Service class to handle business logic for projects
 * */

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    RuleService ruleService;
    
    @Autowired 
    EventService eventService;


    // TODO add repo jpa save later
    private static HashMap<String, JEProject> loadedProjects = new HashMap<String, JEProject>();

    /*
     * Add a new project
     */
    public void saveProject(JEProject project) {
        // Todo add repo jpa save operation
        synchronized (projectRepository) {
            projectRepository.save(project);
            loadedProjects.put(project.getProjectId(), project);
        }

    }

    /*
     * Most likely won't be used
     */
    public void removeProject(String id) throws ProjectNotFoundException {
        // TODO remove project from jpa db

        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        loadedProjects.remove(id);
    }

    /*
     * Add a workflow to project
     */
    public void addWorkflowToProject(JEWorkflow wf) throws ProjectNotFoundException {
        JELogger.trace(ProjectService.class, "Adding workflow with id = " + wf.getJobEngineElementID() + " to project with id = " + wf.getJobEngineProjectID());
        workflowService.addWorkflow(wf);
        saveProject(getProjectById(wf.getJobEngineProjectID()));
    }

    /*
     * Remove workflow from project
     */
    public void deleteWorkflowFromProject(String projectId, String workflowId)
            throws ProjectNotFoundException, WorkflowNotFoundException {
        workflowService.removeWorkflow(projectId, workflowId);
        saveProject(getProjectById(projectId));
    }

    /*
     * Add a rule to project
     */
    public void addRuleToProject(UserDefinedRule rule) throws RuleAlreadyExistsException {
        loadedProjects.get(rule.getJobEngineProjectID()).addRule(rule);
    }

    /*
     * Get all loaded Projects
     */
    public static HashMap<String, JEProject> getLoadedProjects() {
        return loadedProjects;
    }

    /*
     * Return a project loaded in memory
     */
    public static JEProject getProjectById(String id) {
        return loadedProjects.get(id);

    }

    /*
     * Set loaded project in memory
     */
    public static void setLoadedProjects(HashMap<String, JEProject> loadedProjects) {
        ProjectService.loadedProjects = loadedProjects;
    }

    /*
     * Build a workflow by id
     */
    public void buildWorkflow(String projectId, String workflowId)
            throws WorkflowNotFoundException, ProjectNotFoundException, IOException, JERunnerErrorException {
        workflowService.buildWorkflow(projectId, workflowId);
    }

    /*
     * Run a workflow by id
     */
    public void runWorkflow(String projectId, String workflowId)
            throws ProjectNotFoundException, IOException, WorkflowNotFoundException, WorkflowAlreadyRunningException {
        workflowService.runWorkflow(projectId, workflowId);
    }

    /*
     * Builds all the rules and workflows
     */
    public void buildAll(String projectId)
            throws ProjectNotFoundException, IOException, RuleBuildFailedException,
            JERunnerErrorException {
        JELogger.trace(ProjectService.class, "Building the project with id = " + projectId);
        ruleService.buildRules(projectId);
        workflowService.buildWorkflows(projectId);
        loadedProjects.get(projectId).setBuilt(true);

    }

    /*
     * run project => send request to jeRunner to run project
     */
    public void runAll(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectRunException, IOException {
        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JERunnerAPIHandler.runProject(projectId);
                    project.setRunning(true);
                } else {
                    throw new ProjectRunException(Errors.PROJECT_RUNNING);
                }
            } else {
                throw new ProjectRunException(Errors.PROJECT_NOT_BUILT);
            }
        } else {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

    }

    /*
     * Stop a running project
     * */
    public void stopProject(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectRunException, ProjectStatusException, IOException {
        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        if (project.isRunning()) {
            JERunnerAPIHandler.stopProject(projectId);
            project.setRunning(false);
        } else {
            throw new ProjectStatusException(ResponseCodes.PROJECT_STOPPED, Errors.PROJECT_STOPPED);
        }

    }

    /* Return all currently available workflows in project */
    public HashMap<String, JEWorkflow> getAllWorkflows(String projectId) throws ProjectNotFoundException {
        if (loadedProjects.containsKey(projectId)) {
            return loadedProjects.get(projectId).getWorkflows();
        } else throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
    }

    /* Return a workflow by id */
    public JEWorkflow getWorkflowById(String projectId, String key) throws ProjectNotFoundException {
        if (loadedProjects.containsKey(projectId)) {
            return loadedProjects.get(projectId).getWorkflows().get(key);
        } else throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
    }

    /*
     * Return project by id
     */
    public JEProject getProject(String projectId) throws ProjectNotFoundException, JERunnerErrorException, IOException {
        if (!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p = projectRepository.findById(projectId);
            JEProject project = p.isEmpty() ? null : p.get();
            if (project != null) {

                loadedProjects.put(projectId, project);
            }
            for(JEEvent event : project.getEvents().values())
            {
            	eventService.registerEvent(event);
            }
        }
        JELogger.trace(ProjectService.class, "Found project with id = " + projectId);
        return loadedProjects.get(projectId);
    }

	public Collection<?> getAllProjects() {
		List<JEProject> projects = projectRepository.findAll();
		for(JEProject project : projects)
		{
			if(!loadedProjects.containsKey(project.getProjectId()))
			{
				loadedProjects.put(project.getProjectId(), project);
			}
			 //TODO: register events? maybe!
		}
		return projects;
		
	}

}
