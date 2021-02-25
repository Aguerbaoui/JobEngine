package io.je.project.services;

import io.je.classbuilder.entity.JEClass;
import io.je.project.beans.JEProject;
import io.je.project.enums.ProjectStatus;
import io.je.project.exception.JEExceptionHandler;
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
import java.util.concurrent.*;
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

    static boolean runnerStatus = true;

    /* project management */

    private static ConcurrentHashMap<String, JEProject> loadedProjects = new ConcurrentHashMap<>();

    /*
     * Add a new project
     */
    @Async
    public CompletableFuture<Void>  saveProject(JEProject project) {
        //TODO: add test to see if project already exists
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
    public CompletableFuture<Void>  removeProject(String id) throws ProjectNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException, IOException {
        JELogger.trace(ProjectService.class, "deleting project with id = " + id);

        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
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
    
    public static void  setLoadedProjects(ConcurrentHashMap<String, JEProject> loadedProjects) {
        ProjectService.loadedProjects = loadedProjects;

    }

    /*
     * Builds all the rules and workflows
     */
  
    public void  buildAll(String projectId)
            throws ProjectNotFoundException, IOException, RuleBuildFailedException,
            JERunnerErrorException, InterruptedException, ExecutionException, RuleNotFoundException {
        JELogger.trace(ProjectService.class, "Building the project with id = " + projectId);
        CompletableFuture<?> buildRules = ruleService.buildRules(projectId);
        CompletableFuture<?> buildWorkflows = workflowService.buildWorkflows(projectId);
        CompletableFuture.allOf(buildRules,buildWorkflows).join();
        loadedProjects.get(projectId).setProjectStatus(ProjectStatus.built);
        saveProject(projectId).get();


    }

    /*
     * run project => send request to jeRunner to run project
     */
    public void  runAll(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectRunException, IOException, InterruptedException, ExecutionException {
        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JERunnerAPIHandler.runProject(projectId);
                    project.setProjectStatus(ProjectStatus.running);
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
     * */
    public void  stopProject(String projectId)
            throws ProjectNotFoundException, JERunnerErrorException, ProjectStatusException, IOException, InterruptedException, ExecutionException {
        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        if (project.isRunning()) {
            JERunnerAPIHandler.stopProject(projectId);
            project.setProjectStatus(ProjectStatus.stopped);
            saveProject(projectId).get();

        } else {
            throw new ProjectStatusException(Errors.PROJECT_STOPPED);
        }

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
                project.setProjectStatus(ProjectStatus.notBuilt);
                loadedProjects.put(projectId, project);
                for(JEEvent event : project.getEvents().values())
                {
                    //TODO update event types
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
			
			//TODO: to be deleted. 
			if(!loadedProjects.containsKey(project.getProjectId()))
			{
                project.setProjectStatus(ProjectStatus.notBuilt);
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


    /* Return all currently available workflows in project */
    public ConcurrentMap<String, JEWorkflow> getAllWorkflows(String projectId) throws ProjectNotFoundException {
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


    
    //TODO : move to config service
    //########################################### **BUILDER** ################################################################

    public void initialize() throws  InterruptedException, JERunnerErrorException, ExecutionException, IOException, RuleBuildFailedException, RuleNotFoundException, ProjectRunException, AddClassException, ClassLoadException, DataDefinitionUnreachableException {
        classService.loadAllClasses();
    }

    public void updateRunner() {

        new Thread(() -> {
            try {
                boolean serverUp = false;
                while (!serverUp) {
                    Thread.sleep(2000);
                    serverUp = checkRunnerHealth();
                }

                for (JEClass clazz : classService.getLoadedClasses().values()) {
                    classService.addClassToJeRunner(clazz);
                }

                JELogger.info(ProjectService.class, "Runner is up, updating now");
                for (JEProject project : loadedProjects.values()) {
                    for (JEEvent event : project.getEvents().values()) {
                        eventService.updateEventType(project.getProjectId(), event.getJobEngineElementID(), event.getType().toString());
                    }

                    if (project.isBuilt()) {
                    	project.setProjectStatus(ProjectStatus.notBuilt);
                        buildAll(project.getProjectId());
                    }
                    if (project.isRunning()) {
                    	project.setProjectStatus(ProjectStatus.stopped);
                        runAll(project.getProjectId());
                    }
                }
            }
            catch (Exception e) {
                JEExceptionHandler.handleException(e);
            }
        }).start();

    }
    private boolean checkRunnerHealth()  {
        try {
            runnerStatus =  JERunnerAPIHandler.checkRunnerHealth();
        } catch (InterruptedException | JERunnerErrorException | ExecutionException | IOException e) {
            JEExceptionHandler.handleException(e);
            return false;
        }
        return runnerStatus;
    }


    public static boolean isRunnerStatus() {
        return runnerStatus;
    }

    public static void setRunnerStatus(boolean status) {
        runnerStatus = status;
    }

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
    public CompletableFuture<Void>  closeProject(String id) throws ProjectNotFoundException, InterruptedException, JERunnerErrorException, ExecutionException, IOException {
        JELogger.trace(ProjectService.class, "closing project with id = " + id);

        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        JERunnerAPIHandler.cleanProjectDataFromRunner(id);
        loadedProjects.remove(id);
		return CompletableFuture.completedFuture(null);

    }
}
