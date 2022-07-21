package io.je.project.services;

import io.je.project.beans.JEProject;
import io.je.project.config.LicenseProperties;
import io.je.project.repository.ProjectRepository;
import io.je.rulebuilder.components.JERule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.*;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.LibModel;
import io.je.utilities.ruleutils.OperationStatusDetails;
import io.siothconfig.SIOTHConfigUtility;
import models.JEWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.string.StringUtilities;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.JEMessages.BUILT_EVERYTHING_SUCCESSFULLY;
/*
 * Service class to handle business logic for projects
 * */

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    @Lazy
    WorkflowService workflowService;

    @Autowired
    @Lazy
    RuleService ruleService;

    @Autowired
    @Lazy
    EventService eventService;

    @Autowired
    @Lazy
    VariableService variableService;

    @Autowired
    @Lazy
    ClassService classService;

    /* project management */

    private static ConcurrentHashMap<String, JEProject> loadedProjects = new ConcurrentHashMap<>();

    /*
     * Add a new project
     */
    @Async
    public CompletableFuture<Void> saveProject(JEProject project) {
        JELogger.debug("[project = " + project.getProjectName() + "]" + JEMessages.CREATING_PROJECT,
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
    public CompletableFuture<Void> removeProject(String id) {

        try {
            stopProject(id);
        } catch (ProjectNotFoundException | ProjectStopException | LicenseNotActiveException | ExecutionException exp) {
            exp.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
        }
        JELogger.info("[project= " + loadedProjects.get(id)
                        .getProjectName() + "]" + JEMessages.DELETING_PROJECT,
                LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, null);
        try {
            JERunnerAPIHandler.cleanProjectDataFromRunner(id);
        } catch (JERunnerErrorException exp) {
            exp.printStackTrace();
        }

        try {
            ruleService.deleteAll(id);
        } catch (Exception e) {
            JELogger.error("Error deleting rules", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            workflowService.deleteAll(id);
        } catch (Exception e) {
            JELogger.error("Error deleting workflows", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            eventService.deleteEvents(id, null);
        } catch (Exception e) {
            JELogger.error("Error deleting events", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            variableService.deleteVariables(id, null);
        } catch (Exception e) {
            JELogger.error("Error deleting variables", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            projectRepository.deleteById(id);
        } catch (Exception e) {
            JELogger.error("Error deleting project from database", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER,
                    id);
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

    public JEProject getProjectById(String id)
            throws ProjectNotFoundException, ProjectLoadException, LicenseNotActiveException {
        return loadedProjects.containsKey(id) ? loadedProjects.get(id) : this.getProject(id);

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

    public List<OperationStatusDetails> buildAll(String projectId) throws ProjectNotFoundException,
            InterruptedException, ExecutionException, LicenseNotActiveException, ProjectLoadException {
        JEProject project = getProject(projectId);
        JELogger.info("[project= " + project.getProjectName() + "] " + JEMessages.BUILDING_PROJECT,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
//CompletableFuture<?> buildRules = ruleService.compileALLRules(projectId);
        CompletableFuture<List<OperationStatusDetails>> buildWorkflows = workflowService.buildWorkflows(projectId,
                null);
        CompletableFuture<List<OperationStatusDetails>> buildRules = ruleService.compileRules(projectId, null);
        List<OperationStatusDetails> results = new ArrayList<>();
        buildWorkflows.thenApply(operationStatusDetails -> {
                    results.addAll(operationStatusDetails);
                    return results;

                })
                .get();
        buildRules.thenApply(operationStatusDetails -> {
                    results.addAll(operationStatusDetails);
                    return results;

                })
                .get();
        loadedProjects.get(projectId)
                .setBuilt(true);
        saveProject(projectId).get();
        JELogger.debug(BUILT_EVERYTHING_SUCCESSFULLY, LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
        return results;
    }

    /*
     * run project => send request to jeRunner to run project
     */
    public void runAll(String projectId)
            throws ProjectNotFoundException, ProjectRunException, InterruptedException, ExecutionException {

        if (loadedProjects.containsKey(projectId)) {
            JEProject project = loadedProjects.get(projectId);
            if (project.isBuilt()) {
                if (!project.isRunning()) {
                    JELogger.info("[project= " + project.getProjectName() + "]" + JEMessages.RUNNING_PROJECT,
                            LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
                    try {
                        ruleService.buildRules(projectId);

                        JERunnerAPIHandler.runProject(projectId, project.getProjectName());

                        ruleService.updateRulesStatus(projectId, true);

                        project.getRuleEngine().setRunning(true);

                    } catch (Exception exp) {
                        throw new ProjectRunException(JEMessages.ERROR_RUNNING_PROJECT + Arrays.toString(exp.getStackTrace()));
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
    public void stopProject(String projectId) throws ProjectNotFoundException, InterruptedException, ExecutionException,
            ProjectStopException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        if (!loadedProjects.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEProject project = loadedProjects.get(projectId);
        // if (project.isRunning()) {
        JELogger.control("[project = " + project.getProjectName() + "] " + JEMessages.STOPPING_PROJECT,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);

        try {
            JERunnerAPIHandler.stopProject(projectId, project.getProjectName());
            ruleService.stopRules(projectId, null);

        } catch (Exception e) {
            throw new ProjectStopException(JEMessages.ERROR_STOPPING_PROJECT);
        }
        project.setRunning(false);
        saveProject(projectId).get();

    }

    /*
     * Return project by id
     */

    public JEProject getProject(String projectId)
            throws ProjectNotFoundException, LicenseNotActiveException, ProjectLoadException {

        JEProject project = null;

        JELogger.trace("[projectId= " + projectId + "]" + JEMessages.LOADING_PROJECT,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);

        Optional<JEProject> p = projectRepository.findById(projectId);
        if (p.isEmpty()) {
            // FIXME to be removed; so throw exception;
            //  but ascendant compatibility issue; should check with product owner
            p = projectRepository.findByProjectName(projectId);
            if (p.isEmpty()) {
                throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
            }
        }

        project = p.get();
        // FIXME does we nedd to reload all if loadedProjects filled?
        if (!loadedProjects.containsKey(project.getProjectId())) {
            project.setEvents(eventService.getAllJEEvents(project.getProjectId()));
            project.setRules(ruleService.getAllJERules(project.getProjectId()));
            project.setVariables(variableService.getAllJEVariables(project.getProjectId()));
            project.setWorkflows(workflowService.getAllJEWorkflows(project.getProjectId()));
            project.setConfigurationPath(ConfigurationConstants.PROJECTS_PATH + project.getProjectName());
            project.setBuilt(false);
            loadedProjects.put(project.getProjectId(), project);
            for (JEEvent event : project.getEvents()
                    .values()) {
                try {
                    eventService.registerEvent(event);
                } catch (EventException e) {
                    throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                }
            }
            for (JEVariable variable : project.getVariables()
                    .values()) {
                try {
                    variableService.addVariableToRunner(variable);
                } catch (JERunnerErrorException e) {
                    throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                }

            }
            JELogger.debug("[project= " + project.getProjectName() + "] " + JEMessages.PROJECT_FOUND,
                    LogCategory.DESIGN_MODE, project.getProjectId(), LogSubModule.JEBUILDER, null);

            saveProject(project);
        }
        return loadedProjects.get(project.getProjectId());
    }

    /*
     * Save project in DB
     * */
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

    /*
     * Check if project exists
     * */
    public boolean projectExists(String projectId) {
        if (!loadedProjects.containsKey(projectId)) {
            Optional<JEProject> p = projectRepository.findById(projectId);
            return p.isPresent();
        }

        return true;
    }

    /*
     * Load all projects
     * */
    @Async
    public CompletableFuture<Void> loadAllProjects() {

        JELogger.info(JEMessages.LOADING_PROJECTS, LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);
        List<JEProject> projects = projectRepository.findAll();
        for (JEProject project : projects) {
            try {
                Optional<JEProject> p = projectRepository.findById(project.getProjectId());
                project = p.isEmpty() ? null : p.get();
                if (project != null) {
                    project.setEvents(eventService.getAllJEEvents(project.getProjectId()));
                    project.setRules(ruleService.getAllJERules(project.getProjectId()));
                    project.setVariables(variableService.getAllJEVariables(project.getProjectId()));
                    project.setWorkflows(workflowService.getAllJEWorkflows(project.getProjectId()));
                    // project.setBuilt(false);
                    loadedProjects.put(project.getProjectId(), project);
                    for (JEEvent event : project.getEvents()
                            .values()) {
                        try {
                            eventService.registerEvent(event);
                        } catch (EventException e) {
                            throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                        }
                    }
                    for (JEVariable variable : project.getVariables()
                            .values()) {
                        try {
                            variableService.addVariableToRunner(variable);
                        } catch (JERunnerErrorException e) {
                            throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR);
                        }
                    }

                    boolean runStatus = project.isRunning();

                    project.setBuilt(false);
                    project.setRunning(false);
                    for (JERule rule : project.getRules()
                            .values()) {
                        rule.setRunning(false);
                        rule.setBuilt(false);
                        rule.setCompiled(false);
                        rule.setAdded(false);
                        if (rule.getStatus() != Status.ERROR) {
                            rule.setStatus(Status.NOT_BUILT);
                        }
                        ruleService.saveRule(rule);
                    }
                    saveProject(project);

                    if (project.isAutoReload() && runStatus) {
                        buildAll(project.getProjectId());
                        runAll(project.getProjectId());

                    }

                }
            } catch (Exception e) {
                JELogger.warn("[project = " + project.getProjectName() + "]" + JEMessages.FAILED_TO_LOAD_PROJECT,
                        LogCategory.DESIGN_MODE, project.getProjectId(), LogSubModule.JEBUILDER, null);

            }

        }
        return CompletableFuture.completedFuture(null);

    }

    /*
     * inform message from workflow in runtime
     */
    public void informUser(InformModel informBody) {
        new Thread(() -> {
            try {
                String wfId = null;
                Optional<JEProject> p = projectRepository.findByProjectName(informBody.getProjectName());
                if (informBody.getWorkflowName() != null && p.isPresent()) {
                    JEProject project = p.get();
                    List<JEWorkflow> wfs = workflowService.getWorkflowByName(informBody.getWorkflowName());
                    for (JEWorkflow wf : wfs) {
                        if (wf.getJobEngineProjectID()
                                .equals(project.getProjectId())) {
                            JELogger.info(informBody.getMessage(), LogCategory.RUNTIME, wf.getJobEngineProjectID(),
                                    LogSubModule.WORKFLOW, wf.getJobEngineElementID());
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                JELogger.error("Failed to send inform message to tracker", LogCategory.RUNTIME,
                        informBody.getProjectName(), LogSubModule.WORKFLOW, informBody.getWorkflowName());
            }
        }).start();

    }

    /*
     * Send log message to tracker
     * */
    public void sendLog(LogMessage logMessage) {
        new Thread(() -> {
            JELogger.sendLog(logMessage);
        }).start();

    }

    /**
     * Clean up job engine data
     */
    public void cleanUpHouse() {
        List<JEProject> projects = projectRepository.findAll();
        try {
            for (JEProject project : projects) {
                stopProject(project.getProjectId());
            }
            eventService.cleanUpHouse();
            ruleService.cleanUpHouse();
            workflowService.cleanUpHouse();
            variableService.cleanUpHouse();
            classService.cleanUpHouse();
            projectRepository.deleteAll();
            FileUtilities.deleteDirectory(ConfigurationConstants.PROJECTS_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Upload file
     * */
    public JELib addFile(LibModel libModel) throws LibraryException {
        JELogger.control(JEMessages.ADDING_FILE_TO_PROJECT, LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER,
                libModel.getFileName());
        try {
            MultipartFile file = libModel.getFile();
            String orgName = file.getOriginalFilename();
            if (file.getSize() > SIOTHConfigUtility.getSiothConfig()
                    .getJobEngine()
                    .getLibraryMaxFileSize()) {
                JELogger.trace("File size = " + file.getSize());
                throw new LibraryException(JEMessages.FILE_TOO_LARGE);
            }
            if (!file.isEmpty()) {
                /*
                 * if (!FileUtilities.fileIsJar(orgName)) { throw new
                 * LibraryException(JEMessages.JOB_ENGINE_ACCEPTS_JAR_FILES_ONLY); }
                 */
                String uploadsDir = ConfigurationConstants.EXTERNAL_LIB_PATH;
                if (!new File(uploadsDir).exists()) {
                    new File(uploadsDir).mkdir();
                }

                String filePath = uploadsDir + orgName;
                File dest = new File(filePath);
                if (dest.exists()) {
                    FileUtilities.deleteFileFromPath(filePath);
                    // throw new LibraryException(JEMessages.LIBRARY_EXISTS);
                }
                file.transferTo(dest);
                JELogger.debug(JEMessages.UPLOADED_JAR_TO_PATH + dest, LogCategory.DESIGN_MODE, null,
                        LogSubModule.JEBUILDER, null);
                JELib jeLib = new JELib();
                jeLib.setFilePath(dest.getAbsolutePath());
                jeLib.setJobEngineElementName(orgName);
                jeLib.setScope(LibScope.JOBENGINE);
                jeLib.setJeObjectCreatedBy(libModel.getCreatedBy());
                jeLib.setJeObjectModifiedBy(libModel.getCreatedBy());
                jeLib.setJeObjectCreationDate(Instant.now());
                jeLib.setJobEngineElementID(StringUtilities.generateUUID());
                jeLib.setFileType(FileType.valueOf(FileUtilities.getFileExtension(orgName)));
                // libraryRepository.save(jeLib);
                return jeLib;
            }
        } catch (Exception e) {
            throw new LibraryException(JEMessages.ERROR_IMPORTING_FILE + ":" + e.getMessage());
        }

        return null;
    }

}
