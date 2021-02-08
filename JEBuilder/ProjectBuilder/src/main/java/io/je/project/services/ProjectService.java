package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.repository.ProjectRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/*
 * Service class to handle business logic for projects
 * */
import java.util.concurrent.ExecutionException;

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

    
    /* project management */

    private static ConcurrentHashMap<String, JEProject> loadedProjects = new ConcurrentHashMap<>();

    /*
     * Add a new project
     */
    @Async
    public CompletableFuture<Void>  saveProject(JEProject project) {
        // Todo add repo jpa save operation
        synchronized (projectRepository) {
            projectRepository.save(project);
           
        }
        loadedProjects.put(project.getProjectId(), project);
		return CompletableFuture.completedFuture(null);


    }

    /*
     * delete project
     */
    @Async
    public CompletableFuture<Void>  removeProject(String id) throws ProjectNotFoundException {
        // TODO remove project from jpa db
        JELogger.trace(ProjectService.class, "deleting project with id = " + id);

        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        synchronized (projectRepository) {
            projectRepository.deleteById(id);
        }
        loadedProjects.remove(id);
		return CompletableFuture.completedFuture(null);

    }
    

    /*
     * Get all loaded Projects
     */
    
    public static ConcurrentMap<String, JEProject> getLoadedProjects() {
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
    @Async
    public static CompletableFuture<Void>  setLoadedProjects(ConcurrentHashMap<String, JEProject> loadedProjects) {
        ProjectService.loadedProjects = loadedProjects;
		return CompletableFuture.completedFuture(null);

    }

    /*
     * Builds all the rules and workflows
     */
    @Async
    public CompletableFuture<Void>  buildAll(String projectId)
            throws ProjectNotFoundException, IOException, RuleBuildFailedException,
            JERunnerErrorException, InterruptedException, ExecutionException, RuleNotFoundException {
        JELogger.trace(ProjectService.class, "Building the project with id = " + projectId);
        CompletableFuture<?> buildRules = ruleService.buildRules(projectId);
        CompletableFuture<?> buildWorkflows = workflowService.buildWorkflows(projectId);
        CompletableFuture.allOf(buildRules,buildWorkflows).join();
        loadedProjects.get(projectId).setBuilt(true);
        saveProject(projectId).get();
		return CompletableFuture.completedFuture(null);


    }

    /*
     * run project => send request to jeRunner to run project
     */
    @Async
    public CompletableFuture<Void>  runAll(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectRunException, IOException, InterruptedException, ExecutionException {
        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JERunnerAPIHandler.runProject(projectId);
                    project.setRunning(true);
                    saveProject(projectId).get();
                } else {
                    throw new ProjectRunException(Errors.PROJECT_RUNNING);
                }
            } else {
                throw new ProjectRunException(Errors.PROJECT_NOT_BUILT);
            }
        } else {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
		return CompletableFuture.completedFuture(null);

    }

    /*
     * Stop a running project
     * */
    @Async
    public CompletableFuture<Void>  stopProject(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectRunException, ProjectStatusException, IOException, InterruptedException, ExecutionException {
        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        if (project.isRunning()) {
            JERunnerAPIHandler.stopProject(projectId);
            project.setRunning(false);
            saveProject(projectId).get();

        } else {
            throw new ProjectStatusException(ResponseCodes.PROJECT_STOPPED, Errors.PROJECT_STOPPED);
        }
		return CompletableFuture.completedFuture(null);

    }

    /*
     * Return project by id
     */
    @Async
    public CompletableFuture<JEProject> getProject(String projectId) throws ProjectNotFoundException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        if (!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p = projectRepository.findById(projectId);
            JEProject project = p.isEmpty() ? null : p.get();
            if (project != null) {

                loadedProjects.put(projectId, project);
                for(JEEvent event : project.getEvents().values())
                {
                	eventService.registerEvent(event);
                }
            }
          
        }
        JELogger.trace(ProjectService.class, "Found project with id = " + projectId);
        return CompletableFuture.completedFuture(loadedProjects.get(projectId));
    }

	public CompletableFuture<Collection<?>> getAllProjects() {
		List<JEProject> projects = projectRepository.findAll();
		for(JEProject project : projects)
		{
			if(!loadedProjects.containsKey(project.getProjectId()))
			{
				loadedProjects.put(project.getProjectId(), project);
			}
			 //TODO: register events? maybe!
		}
		return CompletableFuture.completedFuture(projects);
		
	}

	 @Async
    public CompletableFuture<Void>  saveProject(String projectId) {
        synchronized (projectRepository) {
            projectRepository.save(loadedProjects.get(projectId));           
        }
		return CompletableFuture.completedFuture(null);
    }
    
    
  //########################################### **Workflows** ################################################################


    /*
     * Add a workflow to project
     */
    @Async
    public CompletableFuture<Void>  addWorkflowToProject(JEWorkflow wf) throws ProjectNotFoundException, InterruptedException, ExecutionException {
        JELogger.trace(ProjectService.class, "Adding workflow with id = " + wf.getJobEngineElementID() + " to project with id = " + wf.getJobEngineProjectID());
    	   workflowService.addWorkflow(wf).get();
           saveProject(getProjectById(wf.getJobEngineProjectID()));
           return CompletableFuture.completedFuture(null);

    }

    /*
     * Remove workflow from project
     */
    @Async
    public CompletableFuture<Void>  deleteWorkflowFromProject(String projectId, String workflowId)
            throws ProjectNotFoundException, WorkflowNotFoundException {
        workflowService.removeWorkflow(projectId, workflowId);
        saveProject(getProjectById(projectId));
		return CompletableFuture.completedFuture(null);

    }



    /*
     * Build a workflow by id
     */
    @Async
    public CompletableFuture<Void>  buildWorkflow(String projectId, String workflowId)
            throws WorkflowNotFoundException, ProjectNotFoundException, IOException, JERunnerErrorException {
        workflowService.buildWorkflow(projectId, workflowId);
		return CompletableFuture.completedFuture(null);

    }

    /*
     * Run a workflow by id
     */
    @Async
    public CompletableFuture<Void>  runWorkflow(String projectId, String workflowId)
            throws ProjectNotFoundException, IOException, WorkflowNotFoundException, WorkflowAlreadyRunningException {
        workflowService.runWorkflow(projectId, workflowId);
		return CompletableFuture.completedFuture(null);

    }


    /* Return all currently available workflows in project */
    @Async
    public CompletableFuture<ConcurrentMap<String, JEWorkflow>> getAllWorkflows(String projectId) throws ProjectNotFoundException {
        if (loadedProjects.containsKey(projectId)) {
            return CompletableFuture.completedFuture(loadedProjects.get(projectId).getWorkflows());
        } else throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
    }

    /* Return a workflow by id */
    @Async
    public CompletableFuture<JEWorkflow> getWorkflowById(String projectId, String key) throws ProjectNotFoundException {
        if (loadedProjects.containsKey(projectId)) {
            return CompletableFuture.completedFuture(loadedProjects.get(projectId).getWorkflows().get(key));
        } else throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
    }


}
