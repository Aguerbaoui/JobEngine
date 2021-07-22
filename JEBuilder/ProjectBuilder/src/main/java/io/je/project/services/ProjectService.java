package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.controllers.ProjectController;
import io.je.project.repository.EventRepository;
import io.je.project.repository.ProjectRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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
    VariableService variableService;

    @Autowired
    ClassService classService;

    @Autowired
    private HttpServletRequest request;

    /* project management */

    private static ConcurrentHashMap<String, JEProject> loadedProjects = new ConcurrentHashMap<>();

    /*
     * Add a new project
     */
    @Async
    public CompletableFuture<Void> saveProject(JEProject project) {
        JELogger.trace( "[projectId= "+project.getProjectId()+"]"+  JEMessages.CREATING_PROJECT);
        synchronized (projectRepository) {
            projectRepository.save(project);
            JELogger.debug(getClass(), "[projectId= "+project.getProjectId()+"]"+  JEMessages.SAVING_PROJECT);
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
		
        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException("[projectId= "+id+"]"+ JEMessages.PROJECT_NOT_FOUND);
        }

        try {
            stopProject(id);
        }
        catch (Exception e) {}
        JELogger.trace("[projectId= "+id+"]"+  JEMessages.DELETING_PROJECT);
        JERunnerAPIHandler.cleanProjectDataFromRunner(id);
    /*    synchronized (projectRepository) {
            projectRepository.deleteById(id);
        }
        */
        loadedProjects.remove(id);
        ruleService.deleteAll(id);
        workflowService.deleteAll(id);
        eventService.deleteAll(id);
        variableService.deleteAll(id);
        projectRepository.deleteById(id);

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
		
        JELogger.trace(ProjectService.class, "[projectId= "+projectId+"]"+  JEMessages.BUILDING_PROJECT);
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
		
        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JELogger.trace("[projectId= "+project.getProjectId()+"]"+  JEMessages.RUNNING_PROJECT);
                    JERunnerAPIHandler.runProject(projectId);
                    project.setRunning(true);
                    saveProject(projectId).get();
                } else {
                    throw new ProjectRunException(JEMessages.PROJECT_RUNNING);
                }
            } else {
                throw new ProjectRunException(JEMessages.PROJECT_NOT_BUILT);
            }
        } else {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

    }

    /*
     * Stop a running project
     */
    public void stopProject(String projectId) throws ProjectNotFoundException, JERunnerErrorException,
            ProjectStatusException, IOException, InterruptedException, ExecutionException, ConfigException {
		
        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        if (project.isRunning()) {
            JELogger.trace("[projectId= "+project.getProjectId()+"]"+  JEMessages.STOPPING_PROJECT);
            JERunnerAPIHandler.stopProject(projectId);
            project.setRunning(false);
            saveProject(projectId).get();

        } else {
            JELogger.error(getClass(), JEMessages.PROJECT_ALREADY_STOPPED +" " + projectId);
            throw new ProjectStatusException(JEMessages.PROJECT_ALREADY_STOPPED);
        }

    }

    /*
     * Return project by id
     */
    
    public JEProject getProject(String projectId) throws ProjectNotFoundException,
            JERunnerErrorException, IOException, InterruptedException, ExecutionException, ConfigException {
    	
    	JEProject project = null;
        JELogger.debug("[projectId= "+projectId+"]"+  JEMessages.LOADING_PROJECT);
        if (!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p = projectRepository.findById(projectId);
             project = p.isEmpty() ? null : p.get();
            if (project != null) {
            	project.setEvents(eventService.getAllJEEvents(projectId));
            	project.setRules(ruleService.getAllJERules(projectId));
            	project.setVariables(variableService.getAllJEVariables(projectId));
            	project.setWorkflows(workflowService.getAllJEWorkflows(projectId));    	
                project.setBuilt(false);
                loadedProjects.put(projectId, project);
                for (JEEvent event : project.getEvents().values()) {
                    eventService.registerEvent(event);
                }
                for(JEVariable variable : project.getVariables().values())
                {
               	 variableService.addVariableToRunner(variable);
                }
            }else {
            	throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
            }
            saveProject(project);
        }
        JELogger.debug("[projectId= "+projectId+"]"+  JEMessages.PROJECT_FOUND);
        return loadedProjects.get(projectId);
    }

    public CompletableFuture<Collection<?>> getAllProjects() throws ConfigException {
		
        JELogger.trace(getClass(), JEMessages.LOADING_PROJECTS);
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
        JELogger.trace("[projectId= "+projectId+"]"+  JEMessages.LOADING_WFS);
        if (loadedProjects.containsKey(projectId)) {
            return loadedProjects.get(projectId).getWorkflows();
        } else
            throw new ProjectNotFoundException("[projectId= "+projectId+"]"+  JEMessages.PROJECT_NOT_FOUND);
    }

    /* Return a workflow by id */
    public JEWorkflow getWorkflowById(String projectId, String key) throws ProjectNotFoundException {
        JELogger.trace(JEMessages.LOADING_WF + " id = " + key + " in project id = " + projectId);
        if (loadedProjects.containsKey(projectId)) {
            return loadedProjects.get(projectId).getWorkflows().get(key);
        } else
            throw new ProjectNotFoundException("[projectId= "+projectId+"]"+  JEMessages.PROJECT_NOT_FOUND);
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
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.trace(ProjectService.class, "[projectId= "+id+"]"+  JEMessages.CLOSING_PROJECT);
        JERunnerAPIHandler.cleanProjectDataFromRunner(id);
        loadedProjects.remove(id);
        return CompletableFuture.completedFuture(null);

    }

    public void resetProjects()
            throws ProjectNotFoundException, EventException, RuleBuildFailedException, JERunnerErrorException,
            RuleNotFoundException, IOException, InterruptedException, ExecutionException, ProjectRunException, ConfigException {
    	
        loadAllProjects();
        for (JEProject project : loadedProjects.values()) {
            for (JEEvent event : project.getEvents().values()) {
                eventService.registerEvent(event);
            }
            for(JEVariable variable : project.getVariables().values())
            {
           	 variableService.addVariableToRunner(variable);
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
        JELogger.trace(JEMessages.RESETTING_PROJECTS);

    }

    @Async
    public CompletableFuture<Void> loadAllProjects() throws ProjectNotFoundException, JERunnerErrorException,
            IOException, InterruptedException, ExecutionException, ConfigException {
    	
        //loadedProjects = new ConcurrentHashMap<String, JEProject>();
        List<JEProject> projects = projectRepository.findAll();
        for (JEProject project : projects) {
        	 Optional<JEProject> p = projectRepository.findById(project.getProjectId());
             project = p.isEmpty() ? null : p.get();
            if (project != null) {
            	project.setEvents(eventService.getAllJEEvents(project.getProjectId()));
            	project.setRules(ruleService.getAllJERules(project.getProjectId()));
            	project.setVariables(variableService.getAllJEVariables(project.getProjectId()));
            	project.setWorkflows(workflowService.getAllJEWorkflows(project.getProjectId()));    	
                project.setBuilt(false);
                loadedProjects.put(project.getProjectId(), project);
                for (JEEvent event : project.getEvents().values()) {
                    eventService.registerEvent(event);
                }
                for(JEVariable variable : project.getVariables().values())
                {
               	 variableService.addVariableToRunner(variable);
                }
                if(!project.isAutoReload())       		 
                {
             	   project.setBuilt(false);
             	   project.setRunning(false);
             	 saveProject(project);
             	  
                }
            }
        	 
        	 

        }
        return CompletableFuture.completedFuture(null);

    }

    public void addJarToProject(MultipartFile file) throws IOException, InterruptedException, JERunnerErrorException, ExecutionException {
        JELogger.trace( JEMessages.ADDING_JAR_TO_PROJECT);
        if(!file.isEmpty()) {
            String uploadsDir = "/uploads/";
            //TODO change to the path set by the user for classes in sioth
            String realPathtoUploads =  request.getServletContext().getRealPath(uploadsDir);
            if(! new File(realPathtoUploads).exists())
            {
                new File(realPathtoUploads).mkdir();
            }

            String orgName = file.getOriginalFilename();
            String filePath = realPathtoUploads + orgName;
            File dest = new File(filePath);
            file.transferTo(dest);
            JELogger.debug("Uploaded jar to path " + dest);
            HashMap<String, String> payload = new HashMap<>();
            payload.put("name", file.getOriginalFilename());
            payload.put("path", dest.getAbsolutePath());
            JERunnerAPIHandler.addJarToRunner(payload);
        }

    }
}
