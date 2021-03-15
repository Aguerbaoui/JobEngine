package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.controllers.ProjectController;
import io.je.project.repository.ProjectRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
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
import java.util.concurrent.ExecutionException;
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

    @Autowired
    ClassService classService;

    /* project management */

    private static ConcurrentHashMap<String, JEProject> loadedProjects = new ConcurrentHashMap<>();

    /*
     * Add a new project
     */
    @Async
    public CompletableFuture<Void> saveProject(JEProject project) {
        JELogger.trace( "Creating project with id = " + project.getProjectId());
        synchronized (projectRepository) {
            projectRepository.save(project);
            JELogger.debug(getClass(), "saving project with id = " + project.getProjectId());
        }
        loadedProjects.put(project.getProjectId(), project);
        return CompletableFuture.completedFuture(null);

    }

    /*
     * delete project
     */
    @Async
    public CompletableFuture<Void> removeProject(String id) throws ProjectNotFoundException, InterruptedException,
            JERunnerErrorException, ExecutionException, ConfigException {
		ConfigurationService.checkConfig();
        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }

        try {
            stopProject(id);
        }
        catch (Exception e) {}
        JELogger.trace("deleting project with id = " + id);
        JERunnerAPIHandler.cleanProjectDataFromRunner(id);
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

    public static void setLoadedProjects(ConcurrentHashMap<String, JEProject> loadedProjects) {
        ProjectService.loadedProjects = loadedProjects;

    }

    /*
     * Builds all the rules and workflows
     */

    public void buildAll(String projectId) throws ProjectNotFoundException, IOException, RuleBuildFailedException,
            JERunnerErrorException, InterruptedException, ExecutionException, RuleNotFoundException, ConfigException {
		ConfigurationService.checkConfig();
        JELogger.trace(ProjectService.class, "Building the project with id = " + projectId);
        CompletableFuture<?> buildRules = ruleService.buildRules(projectId);
        CompletableFuture<?> buildWorkflows = workflowService.buildWorkflows(projectId);
        CompletableFuture.allOf(buildRules, buildWorkflows).join();
        loadedProjects.get(projectId).setBuilt(true);
        saveProject(projectId).get();

    }

    /*
     * run project => send request to jeRunner to run project
     */
    public void runAll(String projectId) throws ProjectNotFoundException, JERunnerErrorException, ProjectRunException,
            IOException, InterruptedException, ExecutionException, ConfigException {
		ConfigurationService.checkConfig();
        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JELogger.trace(" Running project id = " + projectId);
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

    }

    /*
     * Stop a running project
     */
    public void stopProject(String projectId) throws ProjectNotFoundException, JERunnerErrorException,
            ProjectStatusException, IOException, InterruptedException, ExecutionException, ConfigException {
		ConfigurationService.checkConfig();
        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        if (project.isRunning()) {
            JELogger.trace(" Stopping project id = " + projectId);
            JERunnerAPIHandler.stopProject(projectId);
            project.setRunning(false);
            saveProject(projectId).get();

        } else {
            JELogger.error(getClass(), Errors.PROJECT_STOPPED +" " + projectId);
            throw new ProjectStatusException(Errors.PROJECT_STOPPED);
        }

    }

    /*
     * Return project by id
     */
    @Async
    public CompletableFuture<JEProject> getProject(String projectId) throws ProjectNotFoundException,
            JERunnerErrorException, IOException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
        JELogger.trace(" Loading project from database with id = " + projectId);
        if (!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p = projectRepository.findById(projectId);
            JEProject project = p.isEmpty() ? null : p.get();
            if (project != null) {
                project.setBuilt(false);
                loadedProjects.put(projectId, project);
                for (JEEvent event : project.getEvents().values()) {
                    eventService.registerEvent(event);
                }
            }

        }
        JELogger.trace(" Found project with id = " + projectId);
        return CompletableFuture.completedFuture(loadedProjects.get(projectId));
    }

    public CompletableFuture<Collection<?>> getAllProjects() throws ConfigException {
		ConfigurationService.checkConfig();
        JELogger.trace(getClass(), "loading all projects from database ..");
        List<JEProject> projects = projectRepository.findAll();
        for (JEProject project : projects) {
            // TODO: to be deleted.
            if (!loadedProjects.containsKey(project.getProjectId())) {
                project.setBuilt(false);
                loadedProjects.put(project.getProjectId(), project);
            }
            // TODO: register events? maybe!
        }
        return CompletableFuture.completedFuture(projects);
    }

    @Async
    public CompletableFuture<Void> saveProject(String projectId) {
        synchronized (projectRepository) {
            projectRepository.save(loadedProjects.get(projectId));
        }
        return CompletableFuture.completedFuture(null);
    }

    // ########################################### **Workflows**
    // ################################################################

    /* Return all currently available workflows in project */
    public ConcurrentMap<String, JEWorkflow> getAllWorkflows(String projectId) throws ProjectNotFoundException {
        JELogger.trace(" Getting all workflows from project id = " + projectId);
        if (loadedProjects.containsKey(projectId)) {
            return loadedProjects.get(projectId).getWorkflows();
        } else
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND + "[id = " + projectId + "]");
    }

    /* Return a workflow by id */
    public JEWorkflow getWorkflowById(String projectId, String key) throws ProjectNotFoundException {
        JELogger.trace(" Getting workflow id = " + key + " in project id = " + projectId);
        if (loadedProjects.containsKey(projectId)) {
            return loadedProjects.get(projectId).getWorkflows().get(key);
        } else
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND + "[id = " + projectId + "]");
    }

    // TODO : move to config service
    // ########################################### **BUILDER**
    // ################################################################

    public boolean projectExists(String projectId) {
        if (!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p = projectRepository.findById(projectId);
            return p.isPresent();
        }

        return true;
    }

    /*
     * delete project
     */
    @Async
    public CompletableFuture<Void> closeProject(String id) throws ProjectNotFoundException, InterruptedException,
            JERunnerErrorException, ExecutionException {
        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JELogger.trace(ProjectService.class, "closing project with id = " + id);
        JERunnerAPIHandler.cleanProjectDataFromRunner(id);
        loadedProjects.remove(id);
        return CompletableFuture.completedFuture(null);

    }

    public void resetProjects()
            throws ProjectNotFoundException, EventException, RuleBuildFailedException, JERunnerErrorException,
            RuleNotFoundException, IOException, InterruptedException, ExecutionException, ProjectRunException, ConfigException {
    	ConfigurationService.checkConfig();
        loadAllProjects();
        for (JEProject project : loadedProjects.values()) {
            for (JEEvent event : project.getEvents().values()) {
                eventService.updateEventType(project.getProjectId(), event.getJobEngineElementID(),
                        event.getType().toString());
            }

            if (project.isBuilt()) {
                project.setBuilt(false);
                buildAll(project.getProjectId());
            }
            if (project.isRunning()) {
                project.setRunning(false);
                runAll(project.getProjectId());
            }
        }
        JELogger.trace(" Resetting projects after updating runner");

    }

    @Async
    public CompletableFuture<Void> loadAllProjects() throws ProjectNotFoundException, JERunnerErrorException,
            IOException, InterruptedException, ExecutionException, ConfigException {
    	ConfigurationService.checkConfig();
        loadedProjects = new ConcurrentHashMap<String, JEProject>();
        List<JEProject> projects = projectRepository.findAll();
        for (JEProject project : projects) {
            loadedProjects.put(project.getProjectId(), project);
            for (JEEvent event : project.getEvents().values()) {
                eventService.registerEvent(event);
            }
        }
        return CompletableFuture.completedFuture(null);

    }
}
