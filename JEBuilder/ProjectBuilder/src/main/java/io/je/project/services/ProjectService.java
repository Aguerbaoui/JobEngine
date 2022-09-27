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
import utils.log.LoggerUtils;
import utils.string.StringUtilities;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.JEMessages.BUILT_EVERYTHING_SUCCESSFULLY;
/*
 * Service class to handle business logic for projects
 * */

@Service
public class ProjectService {


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

    /* project management */
    @Autowired
    @Lazy
    ClassService classService;



    /*
     * Get all loaded Projects
     */

    /*
     * Return project by id
     */
    public static JEProject getProjectById(String projectId) throws ProjectNotFoundException {

        return ProjectRepository.getProject(projectId);
    }

    /*
     * Return a project loaded in memory
     */

    /*
     * delete project
     */
    @Async
    public CompletableFuture<Void> removeProject(String id) throws ProjectNotFoundException {

        try {
            stopProject(id);
        } catch (ProjectNotFoundException | ProjectStopException | LicenseNotActiveException |
                 ExecutionException exception) {
            JELogger.logException(exception);
        } catch (InterruptedException exception) {
            JELogger.logException(exception);
            Thread.currentThread()
                    .interrupt();
        }
        JELogger.info("[project= " + ProjectRepository.getProject(id).getProjectName() + "]" + JEMessages.DELETING_PROJECT, LogCategory.DESIGN_MODE, id,
                LogSubModule.JEBUILDER, null);
        try {
            JELogger.info("[project= " + ProjectRepository.getProject(id).getProjectName() + "]" + JEMessages.DELETING_PROJECT, LogCategory.DESIGN_MODE, id,
                    LogSubModule.JEBUILDER, null);
            JERunnerAPIHandler.cleanProjectDataFromRunner(id);
        } catch (JERunnerErrorException | ProjectNotFoundException exception) {
            JELogger.logException(exception);
            JELogger.error("Error cleaning project data from runner", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }

        try {
            ruleService.deleteAll(id);
        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.error("Error deleting rules", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            workflowService.deleteAll(id);
        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.error("Error deleting workflows", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            eventService.deleteEvents(id, null);
        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.error("Error deleting events", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            variableService.deleteVariables(id, null);
        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.error("Error deleting variables", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER, id);
        }
        try {
            ProjectRepository.deleteProject(id);
        } catch (Exception exception) {
            JELogger.logException(exception);
            JELogger.error("Error deleting project from database", LogCategory.DESIGN_MODE, id, LogSubModule.JEBUILDER,
                    id);
        }

        //loadedProjects.remove(id);
        return CompletableFuture.completedFuture(null);

    }

    /*
     * Set loaded project in memory
     */



    /*
     * Builds all the rules and workflows
     */

    /*
     * Stop a running project
     */
    public void stopProject(String projectId) throws ProjectNotFoundException, InterruptedException, ExecutionException,
            ProjectStopException, LicenseNotActiveException {
        LicenseProperties.checkLicenseIsActive();

        JEProject project = ProjectRepository.getProject(projectId);
        // if (project.isRunning()) {
        JELogger.control("[project = " + project.getProjectName() + "] " + JEMessages.STOPPING_PROJECT,
                LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);

        try {
            JERunnerAPIHandler.stopProject(projectId, project.getProjectName());
            ruleService.stopRules(projectId, null);

        } catch (Exception e) {
            LoggerUtils.logException(e);
            throw new ProjectStopException(JEMessages.ERROR_STOPPING_PROJECT);
        }
        project.setRunning(false);


    }
    /*
     * Return project by id
     */

    public JEProject getProject(String projectId) throws ProjectNotFoundException {

        return ProjectRepository.getProject(projectId);
    }

    /*
     * Check if project exists
     * */
    public boolean projectExists(String projectId) {
        return ProjectRepository.projectExists(projectId);
    }

    /*
     * Return project by id
     */

    /*
     * Load all projects
     * */
    @Async
    public CompletableFuture<Void> loadAllProjects() {

        JELogger.info(JEMessages.LOADING_PROJECTS, LogCategory.DESIGN_MODE, null, LogSubModule.JEBUILDER, null);

        List<JEProject> projects = ProjectRepository.getAllProjects(false);

        for (JEProject project : projects) {
            try {
                if (project != null) {
                    project.setEvents(eventService.getAllJEEvents(project.getProjectId()));
                    project.setRules(ruleService.getAllJERules(project.getProjectId()));
                    project.setVariables(variableService.getAllJEVariables(project.getProjectId()));
                    project.setWorkflows(workflowService.getAllJEWorkflows(project.getProjectId()));
                    for (JEEvent event : project.getEvents()
                            .values()) {
                        try {
                            eventService.registerEvent(event);
                        } catch (EventException e) {

                            LoggerUtils.logException(e);

                            throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR + e.getMessage());
                        }
                    }
                    for (JEVariable variable : project.getVariables()
                            .values()) {
                        try {
                            variableService.addVariableToRunner(variable);
                        } catch (JERunnerErrorException e) {

                            LoggerUtils.logException(e);

                            throw new ProjectLoadException(JEMessages.PROJECT_LOAD_ERROR + e.getMessage());
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

                        // FIXME
                        if (rule.getStatus() != Status.ERROR) {
                            rule.setStatus(Status.NOT_BUILT);
                        }

                        ruleService.saveRule(rule);
                    }


                    if (project.isAutoReload() && runStatus) {

                        buildAll(project.getProjectId());

                        runAll(project.getProjectId());

                    }

                }
            } catch (Exception exception) {
                JELogger.logException(exception);

                JELogger.warn("[project = " + project.getProjectName() + "]" + JEMessages.FAILED_TO_LOAD_PROJECT,
                        LogCategory.DESIGN_MODE, project.getProjectId(), LogSubModule.JEBUILDER, null);
            }

        }
        return CompletableFuture.completedFuture(null);

    }

    /*
     * Add a new project
     */


    // TODO : move to config service
    // ########################################### **BUILDER**
    // ################################################################

    public List<OperationStatusDetails> buildAll(String projectId) throws ProjectNotFoundException,
            InterruptedException, ExecutionException, LicenseNotActiveException, ProjectLoadException {

        JEProject project = ProjectRepository.getProject(projectId);

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
        // if there are no workflows or rules that built, then project is not built
        //TODO: to check after project mng changes
        if (results.isEmpty()) {
            ProjectRepository.getProject(projectId).setBuilt(true);
        } else {
            var buildResult = results.stream().anyMatch(OperationStatusDetails::isOperationSucceeded);
            ProjectRepository.getProject(projectId).setBuilt(buildResult);
        }

        JELogger.debug(BUILT_EVERYTHING_SUCCESSFULLY, LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
        return results;
    }

    /*
     * run project => send request to jeRunner to run project
     */
    public void runAll(String projectId)
            throws ProjectNotFoundException, ProjectRunException, InterruptedException, ExecutionException {


        JEProject project = ProjectRepository.getProject(projectId);
        if (project.isBuilt()) {
            if (!project.isRunning()) {
                JELogger.info("[project= " + project.getProjectName() + "]" + JEMessages.RUNNING_PROJECT,
                        LogCategory.DESIGN_MODE, projectId, LogSubModule.JEBUILDER, null);
                try {
                    // FIXME to be removed, no build on run
                    ruleService.buildRules(projectId);

                    JERunnerAPIHandler.runProject(projectId, project.getProjectName());

                    ruleService.updateRulesStatus(projectId, true);

                    project.getRuleEngine().setRunning(true);

                } catch (Exception exception) {
                    JELogger.logException(exception);
                    throw new ProjectRunException(JEMessages.ERROR_RUNNING_PROJECT + exception.getMessage());
                }
                project.setRunning(true);

            } else {
                throw new ProjectRunException(JEMessages.PROJECT_RUNNING);
            }
        } else {
            throw new ProjectRunException(JEMessages.PROJECT_NOT_BUILT);
        }


    }

    public CompletableFuture<Void> saveProject(String projectId) throws ProjectLoadException {
        ProjectRepository.saveProject(projectId);
        return CompletableFuture.completedFuture(null);
    }

    /*
     * inform message from workflow in runtime
     * !is this used?
     */
    public void informUser(InformModel informBody) {
        new Thread(() -> {
            try {
                String wfId = null;
                Optional<JEProject> p = Optional.ofNullable(ProjectRepository.getProject(informBody.getProjectName()));
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
                LoggerUtils.logException(e);
                JELogger.error("Failed to send inform message to tracker : " + e.getMessage(), LogCategory.RUNTIME,
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
        List<JEProject> projects = ProjectRepository.getAllProjects(false);
        try {
            for (JEProject project : projects) {
                stopProject(project.getProjectId());
            }
            eventService.cleanUpHouse();
            ruleService.cleanUpHouse();
            workflowService.cleanUpHouse();
            variableService.cleanUpHouse();
            classService.cleanUpHouse();
            ProjectRepository.deleteAll();
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
            LoggerUtils.logException(e);
            throw new LibraryException(JEMessages.ERROR_IMPORTING_FILE + ":" + e.getMessage());
        }

        return null;
    }

}
