package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.repository.ProjectRepository;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
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

 /*  @Autowired
    ClassService classService;*/

    @Autowired
    private HttpServletRequest request;

    /* project management */

    private static ConcurrentHashMap<String, JEProject> loadedProjects = new ConcurrentHashMap<>();

    /*
     * Add a new project
     */
    @Async
    public CompletableFuture<Void> saveProject(JEProject project) {
        JELogger.debug( "[projectId= "+project.getProjectId()+"]"+  JEMessages.CREATING_PROJECT,
         LogCategory.DESIGN_MODE, project.getProjectId(), LogSubModule.JEBUILDER, null);
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
    public CompletableFuture<Void> removeProject(String id) throws ProjectNotFoundException, InterruptedException,
            JERunnerErrorException, ExecutionException, ConfigException, LicenseNotActiveException {
		
        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException("[projectId= "+id+"]"+ JEMessages.PROJECT_NOT_FOUND);
        }

        try {
            stopProject(id);
        }
        catch (Exception e) {}
        JELogger.info( "[projectId= "+id+"]"+  JEMessages.DELETING_PROJECT,
                LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, null);
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
            JERunnerErrorException, InterruptedException, ExecutionException, RuleNotFoundException, ConfigException, WorkflowBuildException, LicenseNotActiveException {
        JELogger.info( "[projectId= "+projectId+"]"+  JEMessages.BUILDING_PROJECT,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
        CompletableFuture<?> buildRules = ruleService.compileRules(projectId);
        CompletableFuture<?> buildWorkflows = workflowService.buildWorkflows(projectId);
        CompletableFuture.allOf(buildRules, buildWorkflows).join();
        loadedProjects.get(projectId).setBuilt(true);
        saveProject(projectId).get();

    }

    /*
     * run project => send request to jeRunner to run project
     */
    public void runAll(String projectId) throws ProjectNotFoundException, ProjectRunException,
            InterruptedException, ExecutionException, RuleBuildFailedException, RuleNotFoundException, IOException {
		
        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JELogger.info( "[projectId= "+project.getProjectId()+"]"+  JEMessages.RUNNING_PROJECT,
                            LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
                    try {
                        CompletableFuture<?> buildRules = ruleService.buildRules(projectId);
                        buildRules.get();
                        JERunnerAPIHandler.runProject(projectId);
                        ruleService.updateRulesStatus(projectId,true);
          			  	project.getRuleEngine().setRunning(true);

                    }
                    catch(JERunnerErrorException e) {
                        throw new ProjectRunException(JEMessages.ERROR_RUNNING_PROJECT);
                    }
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
    public void stopProject(String projectId) throws ProjectNotFoundException,
            ProjectStatusException, InterruptedException, ExecutionException, ProjectStopException {
		
        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        //if (project.isRunning()) {
            JELogger.info( "[projectId= "+project.getProjectId()+"]"+  JEMessages.STOPPING_PROJECT,
                    LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);

            try {
                JERunnerAPIHandler.stopProject(projectId);
                project.getRuleEngine().setRunning(false);
                ruleService.updateRulesStatus(projectId,false);

            }
            catch (JERunnerErrorException e) {
                throw new ProjectStopException(JEMessages.ERROR_STOPPING_PROJECT);
            }
            project.setRunning(false);
            saveProject(projectId).get();

       /* } else {
            JELogger.error( JEMessages.PROJECT_ALREADY_STOPPED +" " + projectId,
                    LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
            throw new ProjectStatusException(JEMessages.PROJECT_ALREADY_STOPPED);
        }*/

    }

    /*
     * Return project by id
     */
    
    public JEProject getProject(String projectId) throws ProjectNotFoundException,
            JERunnerErrorException, IOException, InterruptedException, ExecutionException, LicenseNotActiveException, ProjectLoadException {
    	
    	JEProject project = null;
        JELogger.debug( "[projectId= "+projectId+"]"+  JEMessages.LOADING_PROJECT,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
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
                    try {
                        eventService.registerEvent(event);
                    }
                    catch(EventException e) {
                        throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                    }
                }
                for(JEVariable variable : project.getVariables().values())
                {
                    try {
                        variableService.addVariableToRunner(variable);
                    }
                    catch(JERunnerErrorException e) {
                        throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                    }

                }
            }else {
            	throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
            }
            saveProject(project);
        }
        JELogger.debug( "[projectId= "+projectId+"]"+  JEMessages.PROJECT_FOUND,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
        return loadedProjects.get(projectId);
    }

  /*  public CompletableFuture<Collection<?>> getAllProjects() {
        JELogger.debug( JEMessages.LOADING_PROJECTS,
                LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);
        List<JEProject> projects = projectRepository.findAll();
        for (JEProject project : projects) {
            if (!loadedProjects.containsKey(project.getProjectId())) {
                project.setBuilt(false);
                loadedProjects.put(project.getProjectId(), project);
            }
        }
        return CompletableFuture.completedFuture(projects);
    }*/

    @Async
    public CompletableFuture<Void> saveProject(String projectId) {
        synchronized (projectRepository) {
            projectRepository.save(loadedProjects.get(projectId));
        }
        return CompletableFuture.completedFuture(null);
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
   /* @Async
    public CompletableFuture<Void> closeProject(String id) throws ProjectNotFoundException, InterruptedException,
            JERunnerErrorException, ExecutionException {
        if (!loadedProjects.containsKey(id)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JELogger.trace(ProjectService.class, "[projectId= "+id+"]"+  JEMessages.CLOSING_PROJECT);
        JERunnerAPIHandler.cleanProjectDataFromRunner(id);
        loadedProjects.remove(id);
        return CompletableFuture.completedFuture(null);

    }*/

    /*public void resetProjects()
            throws ProjectNotFoundException, RuleBuildFailedException, JERunnerErrorException,
            RuleNotFoundException, IOException, InterruptedException, ExecutionException, ProjectRunException, ConfigException, WorkflowBuildException {

        loadAllProjects();
        for (JEProject project : loadedProjects.values()) {
            //we are loading them in loadAllProjects()
           /* for (JEEvent event : project.getEvents().values()) {
                eventService.registerEvent(event);
            }
            for(JEVariable variable : project.getVariables().values())
            {
           	 variableService.addVariableToRunner(variable);
            }*/

          /*  if (project.isBuilt()) {
                project.setBuilt(false);
                buildAll(project.getProjectId());
            }
            if (project.isRunning()) {
                project.setRunning(false);
                runAll(project.getProjectId());
            }
        }
        JELogger.trace(JEMessages.RESETTING_PROJECTS);

    }*/

    @Async
    public CompletableFuture<Void> loadAllProjects() throws ProjectNotFoundException, JERunnerErrorException,
            IOException, InterruptedException, ExecutionException, LicenseNotActiveException, ProjectLoadException {
    	
        //loadedProjects = new ConcurrentHashMap<String, JEProject>();
        JELogger.info( JEMessages.LOADING_PROJECTS,
                LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);
        List<JEProject> projects = projectRepository.findAll();
        for (JEProject project : projects) {
        	 Optional<JEProject> p = projectRepository.findById(project.getProjectId());
             project = p.isEmpty() ? null : p.get();
            if (project != null) {
            	project.setEvents(eventService.getAllJEEvents(project.getProjectId()));
            	project.setRules(ruleService.getAllJERules(project.getProjectId()));
            	project.setVariables(variableService.getAllJEVariables(project.getProjectId()));
            	project.setWorkflows(workflowService.getAllJEWorkflows(project.getProjectId()));    	
                //project.setBuilt(false);
                loadedProjects.put(project.getProjectId(), project);
                for (JEEvent event : project.getEvents().values()) {
                    try {
                        eventService.registerEvent(event);
                    }
                    catch(EventException e) {
                        throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                    }
                }
                for(JEVariable variable : project.getVariables().values())
                {
                    try {
                        variableService.addVariableToRunner(variable);
                    }
                    catch(JERunnerErrorException e) {
                        throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                    }
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

    public void addJarToProject(MultipartFile file) throws LibraryException {
        JELogger.info( JEMessages.ADDING_JAR_TO_PROJECT,
                LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);
        try {
            if (!file.isEmpty()) {
                String uploadsDir = "../uploads/";
                //TODO change to the path set by the user for classes in sioth
                String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
                if (!new File(realPathtoUploads).exists()) {
                    new File(realPathtoUploads).mkdir();
                }

                String orgName = file.getOriginalFilename();
                String filePath = realPathtoUploads + orgName;
                File dest = new File(filePath);
                file.transferTo(dest);
                JELogger.debug(JEMessages.UPLOADED_JAR_TO_PATH + dest,
                        LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);
                HashMap<String, String> payload = new HashMap<>();
                payload.put("name", file.getOriginalFilename());
                payload.put("path", dest.getAbsolutePath());

                JERunnerAPIHandler.addJarToRunner(payload);
            }
        }
            catch(JERunnerErrorException | IOException e ) {
                throw new LibraryException(JEMessages.ERROR_IMPORTING_FILE);
            }
        }


}
