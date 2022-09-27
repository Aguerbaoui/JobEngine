package io.je.runtime.services;

import io.je.JEProcess;
import io.je.project.variables.VariableManager;
import io.je.ruleengine.data.DMListener;
import io.je.ruleengine.data.DataModelListener;
import io.je.runtime.events.EventManager;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RunnerRuleModel;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.serviceTasks.ActivitiTask;
import io.je.serviceTasks.ActivitiTaskManager;
import io.je.utilities.beans.ClassAuthor;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.beans.Status;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.*;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import io.je.utilities.ruleutils.OperationStatusDetails;
import org.springframework.stereotype.Service;
import utils.log.LogCategory;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;

/**
 * Service class to handle JERunner inputs
 */
@Service
public class RuntimeDispatcher {

    // Projects
    // key: projectId , value : true if project is running, false if not
    static Map<String, Boolean> projectStatus = new HashMap<>();
    static Map<String, String> projectNameToId = new HashMap<>();

    ///////////////////////////////// PROJECT

    // build project
	/*public void buildProject(String projectId) throws RuleBuildFailedException, WorkflowBuildException {
		/*JELogger.debug("[projectId  = " + projectId + "]" + JEMessages.BUILDING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);*/
		/*RuleEngineHandler.buildProject(projectId);
		WorkflowEngineHandler.buildProject(projectId);
	}*/


    public static void informUser(String message, String projectName, String workflowName) {
        new Thread(() -> {
            String projectId = projectNameToId.get(projectName);
            try {
                JEProcess process = WorkflowEngineHandler.getProcessByID(projectId, workflowName);
                JELogger.info(message, LogCategory.RUNTIME, projectId,
                        LogSubModule.WORKFLOW, process.getKey());
            } catch (WorkflowBuildException ex) {
                LoggerUtils.logException(ex);
                JELogger.error(message, LogCategory.RUNTIME, projectId,
                        LogSubModule.WORKFLOW, ex.getMessage(), ex.toString());
            }
        }).start();

    }

    public static void sendLog(LogMessage logMessage) {
        new Thread(() -> JELogger.sendLog(logMessage)).start();
    }

    // Run project
    public void runProject(String projectId, String projectName) throws JEException {

        projectStatus.put(projectId, true);

        JELogger.control("[project  = " + projectName + "]" + JEMessages.RUNNING_PROJECT, LogCategory.RUNTIME, projectId,
                LogSubModule.JERUNNER, null);

        try {

            // Reset variables TODO: make it configurable//Same for events
            VariableManager.resetVariableValues(projectId);

            // Run workflows
            WorkflowEngineHandler.runAllWorkflows(projectId, true);

            // Run rules
            RuleEngineHandler.startProjectRuleEngine(projectId); // FIXME should launch all rules not just rule engine

            // Add variables
            for (JEVariable variable : VariableManager.getAllVariables(projectId)) {
                RuleEngineHandler.addVariable(variable);
            }

        } catch (JEException e) {
            LoggerUtils.logException(e);
            JELogger.error(" [project  = " + projectName + "]" + JEMessages.PROJECT_RUN_FAILED, LogCategory.RUNTIME,
                    projectId, LogSubModule.JERUNNER, null);

            RuleEngineHandler.stopProjectRuleEngine(projectId);

            WorkflowEngineHandler.stopProjectWorkflows(projectId);

            projectStatus.put(projectId, false);
            throw e;
        }

    }

    // Stop project
    public void stopProject(String projectId, String projectName) {

        String msg = "[project = " + projectName + "] ";
        // stop workflows
        JELogger.control(msg + JEMessages.STOPPING_PROJECT, LogCategory.RUNTIME, projectId,
                LogSubModule.JERUNNER, null);
        try {

            RuleEngineHandler.stopProjectRuleEngine(projectId);

            WorkflowEngineHandler.stopProjectWorkflows(projectId);

        } catch (WorkflowBuildException ex) {
            LoggerUtils.logException(ex);
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW, ex.getMessage(), ex.toString());
        }

        projectStatus.put(projectId, false);

    }

    /**
     * private int numberOfActiveProjectsByTopic(String topic) { int counter = 0;
     * Set<String> projects = projectsByTopic.get(topic); for (String projectId :
     * projects) { if (Boolean.TRUE.equals(projectStatus.get(projectId))) {
     * counter++; } }
     * <p>
     * return counter; }
     */
    // ***********************************RULES********************************************************

    // add rule
    public void addRule(RunnerRuleModel runnerRuleModel) throws RuleAlreadyExistsException, RuleCompilationException,
            JEFileNotFoundException, RuleFormatNotValidException {

        JELogger.debug(JEMessages.ADDING_RULE + " : ruleId =" + runnerRuleModel.getRuleId(), LogCategory.RUNTIME,
                runnerRuleModel.getProjectId(), LogSubModule.RULE, runnerRuleModel.getRuleId());
        RuleEngineHandler.addRule(runnerRuleModel);
    }

    // update rule
    public void updateRule(RunnerRuleModel runnerRuleModel)
            throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

        JELogger.debug(JEMessages.UPDATING_RULE + " : " + runnerRuleModel.getRuleId(), LogCategory.RUNTIME,
                runnerRuleModel.getProjectId(), LogSubModule.RULE, runnerRuleModel.getRuleId());

        RuleEngineHandler.updateRule(runnerRuleModel);

    }

    // compile rule
    public void compileRule(RunnerRuleModel runnerRuleModel)
            throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {

        JELogger.debug(JEMessages.COMPILING_RULE + " Id : " + runnerRuleModel.getRuleId(), LogCategory.RUNTIME,
                runnerRuleModel.getProjectId(), LogSubModule.RULE, runnerRuleModel.getRuleId());

        RuleEngineHandler.compileRule(runnerRuleModel);

    }

    // ***********************************WORKFLOW********************************************************

    // delete rule
    public void deleteRule(String projectId, String ruleId) throws DeleteRuleException {
        JELogger.debug("[projectId = " + projectId + "] [ruleId = " + ruleId + "] " + JEMessages.DELETING_RULE,
                LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
        RuleEngineHandler.deleteRule(projectId, ruleId);
    }

    public List<OperationStatusDetails> updateRules(List<RunnerRuleModel> runnerRuleModels) {

        List<OperationStatusDetails> updateResult = new ArrayList<>();

        for (RunnerRuleModel runnerRuleModel : runnerRuleModels) {

            OperationStatusDetails details = new OperationStatusDetails(runnerRuleModel.getRuleId());

            removeRuleTopics(runnerRuleModel.getRuleId());

            addTopics(runnerRuleModel.getProjectId(), runnerRuleModel.getRuleId(), "rule", runnerRuleModel.getTopics());

            try {
                updateRule(runnerRuleModel);
                details.setOperationSucceeded(true);
                updateResult.add(details);
            } catch (RuleCompilationException | JEFileNotFoundException | RuleFormatNotValidException e) {
                LoggerUtils.logException(e);
                details.setOperationSucceeded(false);
                details.setOperationError(e.getMessage());
                updateResult.add(details);
                // FIXME finally
                return updateResult;
            }

        }

        return updateResult;

    }

    public void compileRules(List<RunnerRuleModel> runnerRuleModels) throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
        for (RunnerRuleModel runnerRuleModel : runnerRuleModels) {
            compileRule(runnerRuleModel);
        }

    }

    /**
     * Add a workflow to the engine
     */
    public void addWorkflow(WorkflowModel wf) {
        try {
            JELogger.debug("[projectId = " + wf.getProjectId() + "] [workflow = " + wf.getId() + "]" + JEMessages.ADDING_WF,
                    LogCategory.RUNTIME, wf.getProjectId(), LogSubModule.WORKFLOW, wf.getId());

            MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), wf.getName(), ObjectType.JEWORKFLOW,
                    wf.getProjectId(), Status.BUILDING.toString(), Status.BUILDING.toString());

            JEMonitor.publish(msg);

            JEProcess process = new JEProcess(wf.getId(), wf.getName(), wf.getPath(), wf.getProjectId(),
                    wf.isTriggeredByEvent());
            process.setOnProjectBoot(wf.isOnProjectBoot());
            if (wf.isTriggeredByEvent()) {
                process.setTriggerMessage(wf.getTriggerMessage());
            }
            //JobEngine.updateProjects(wf.getProjectId(), wf.getProjectName());
            for (TaskModel task : wf.getTasks()) {
                ActivitiTask activitiTask = WorkflowEngineHandler.parseTask(wf.getProjectId(), wf.getId(), wf.getName(), task);
                ActivitiTaskManager.addTask(activitiTask);
                process.addActivitiTask(activitiTask);
            }
            if (wf.getEndBlockEventId() != null) {
                process.setEndEventId(wf.getEndBlockEventId());
            }
            WorkflowEngineHandler.addProcess(process);
            projectNameToId.put(wf.getProjectName(), wf.getProjectId());

            msg = new MonitoringMessage(LocalDateTime.now(), wf.getName(), ObjectType.JEWORKFLOW,
                    wf.getProjectId(), Status.BUILDING.toString(), Status.STOPPED.toString());

            JEMonitor.publish(msg);

        } catch (Exception e) {
            LoggerUtils.logException(e);

            MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), wf.getName(), ObjectType.JEWORKFLOW,
                    wf.getProjectId(), Status.STOPPED.toString(), Status.STOPPED.toString());

            JEMonitor.publish(msg);
        }
    }

    /**
     * Launch a workflow without variables
     */
    public void launchProcessWithoutVariables(String projectId, String key, boolean runProject)
            throws WorkflowNotFoundException, WorkflowAlreadyRunningException,
            WorkflowBuildException, WorkflowRunException {

        JELogger.debug("[projectId = " + projectId + "] [workflow = " + key + "] " + JEMessages.RUNNING_WF,
                LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, key);
        //buildWorkflow(projectId, key);
        WorkflowEngineHandler.launchProcessWithoutVariables(projectId, key, runProject);

    }

    /**
     * Run all workflows deployed in the engine without project specification
     */
    public void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
        String msg = "[projectId = " + projectId + "]";
        JELogger.debug(msg + JEMessages.RUNNING_WFS, LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW, null);
        try {
            WorkflowEngineHandler.runAllWorkflows(projectId, false);
        } catch (WorkflowBuildException ex) {
            LoggerUtils.logException(ex);
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW, ex.getMessage(), ex.toString());
        }
    }

    /**
     * Deploy a workflow to the engine
     */
    public void buildWorkflow(String projectId, String key) throws WorkflowBuildException {
        JELogger.debug("[projectId = " + projectId + "] [workflow = " + key + "]" + JEMessages.DEPLOYING_WF,
                LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, key);
        WorkflowEngineHandler.deployBPMN(projectId, key);
    }

    ///////////////////////////// Classes
    // add class
    public void addClass(ClassModel classModel, boolean update) throws ClassLoadException {
        JELogger.debug(JEMessages.ADDING_CLASS + ": " + classModel.getClassName(), LogCategory.RUNTIME, null, LogSubModule.CLASS, null);
        String className = JEClassLoader.getJobEnginePackageName(ClassBuilderConfig.CLASS_PACKAGE) + "." + classModel.getClassName();
        JELogger.debug("Class name = " + className);
        try {
            Class<?> c = null;
            JEClassLoader.getDataModelInstance();
            if (classModel.getClassAuthor()
                    .equals(ClassAuthor.DATA_MODEL) && (!JEClassLoader.classIsLoaded(className))) {
                JEClassLoader.addClassToDataModelClassesSet(className);
                c = JEClassLoader.getDataModelInstance()
                        .loadClass(className);
                ClassRepository.addClass(classModel.getClassId(), classModel.getClassName(), c);
            } else if (update) {
                JEClassLoader.overrideDataModelInstance(className);
                JEClassLoader.addClassToDataModelClassesSet(className);
                c = JEClassLoader.getDataModelInstance()
                        .loadClass(className);

                ClassRepository.addClass(classModel.getClassId(), classModel.getClassName(), c);

            }

        } catch (ClassNotFoundException e) {
            LoggerUtils.logException(e);
            //JEClassLoader.removeClassFromDataModelClassesSet(className);
            throw new ClassLoadException(
                    "[class :" + classModel.getClassName() + " ]" + JEMessages.CLASS_LOAD_FAILED);
        }

    }

    public void updateClass(ClassModel classModel) throws ClassLoadException {
        //https://softok.integrationobjects.com/system/modules/Issue/Ticket_Details.aspx?code=14654
        //? reversed adding class with reloading container
        addClass(classModel, true);
        if (classModel.getClassAuthor()
                .equals(ClassAuthor.DATA_MODEL)) {
            RuleEngineHandler.reloadContainers();
        }

    }

    /**
     * add a topic
     */
    public void addTopics(String projectId, String listenerId, String listenerType, Set<String> topics) {
        if (topics != null && !topics.isEmpty()) {
            DMListener dMListener = new DMListener(listenerId, projectId, listenerType);
            DataModelListener.updateDMListener(dMListener, topics);
        }
    }

    // Trigger an event
    public void triggerEvent(String projectId, String id) throws EventException, ProjectNotFoundException {

        EventManager.triggerEvent(projectId, id);

    }

    // Add an event to the runner
    public void addEvent(EventModel eventModel) {
        JEEvent e = new JEEvent(eventModel.getEventId(), eventModel.getProjectId(), eventModel.getName(),
                EventType.valueOf(eventModel.getEventType()), eventModel.getDescription(), eventModel.getTimeout(),
                eventModel.getTimeoutUnit(), eventModel.getCreatedBy(), eventModel.getModifiedBy());
        e.setJobEngineProjectName(eventModel.getProjectName());
        JELogger.debug(
                "[project = " + e.getJobEngineProjectName() + "] [event = " + e.getJobEngineElementName() + "]"
                        + JEMessages.ADDING_EVENT,
                LogCategory.RUNTIME, eventModel.getProjectId(), LogSubModule.EVENT, eventModel.getEventId());

        EventManager.addEvent(eventModel.getProjectId(), e);
    }

    public void updateEventType(String projectId, String eventId, String eventType)
            throws ProjectNotFoundException, EventException {
        JELogger.debug("[projectId = " + projectId + "] [event = " + eventId + "] " + JEMessages.UPDATING_EVENT +
                eventId + " to type = " + eventType, LogCategory.RUNTIME, projectId, LogSubModule.EVENT, eventId);
        EventManager.updateEventType(projectId, eventId, eventType);
    }

    public void deleteEvent(String projectId, String eventId) throws ProjectNotFoundException, EventException {
        JELogger.debug("[projectId = " + projectId + "] [event = " + eventId + "]" + JEMessages.DELETING_EVENT,
                LogCategory.RUNTIME, projectId, LogSubModule.EVENT, eventId);
        EventManager.deleteEvent(projectId, eventId);
    }

    // clean project data from runner
    // Remove events, topics to listen to, rules and workflows
    public void removeProjectData(String projectId) {
        String msg = "[projectId = " + projectId + "]";
        JELogger.debug(msg + JEMessages.DELETING_PROJECT, LogCategory.RUNTIME, projectId,
                LogSubModule.JERUNNER, null);
        EventManager.deleteProjectEvents(projectId);
        try {
            WorkflowEngineHandler.deleteProjectProcesses(projectId);
        } catch (WorkflowBuildException ex) {
            LoggerUtils.logException(ex);
            JELogger.error(msg, LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW, ex.getMessage(), ex.toString());
        }
        RuleEngineHandler.deleteProjectRules(projectId);
        DataModelListener.removeDMListener(projectId);
    }

    // remove rule topics
    public void removeRuleTopics(String ruleId) {

        DataModelListener.removeDMListener(ruleId);

    }

    // remove/stop workflow from runner
    public void removeWorkflow(String projectId, String workflowId) {

        try {
            WorkflowEngineHandler.deleteProcess(projectId, workflowId);
        } catch (WorkflowRunException e) {
            LoggerUtils.logException(e);
            JELogger.debug(JEMessages.ERROR_DELETING_A_NON_EXISTING_PROCESS,
                    LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW, workflowId);
        } catch (WorkflowBuildException ex) {
            LoggerUtils.logException(ex);
            JELogger.error(workflowId, LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW, ex.getMessage(), ex.toString());
        }
    }

    // add variable to runner
    public void addVariable(VariableModel variableModel) throws VariableException {
        JELogger.debug(
                "[project = " + variableModel.getProjectName() + "] [variable = " + variableModel.getName() + "]"
                        + JEMessages.ADDING_VARIABLE,
                LogCategory.RUNTIME, variableModel.getProjectId(), LogSubModule.VARIABLE, variableModel.getId());
        JEVariable var = new JEVariable(variableModel.getId(), variableModel.getProjectId(), variableModel.getName(),
                variableModel.getType(), variableModel.getInitialValue(), variableModel.getDescription(),
                variableModel.getCreatedBy(), variableModel.getModifiedBy());
        var.setJeObjectCreationDate(Instant.now());
        var.setJeObjectLastUpdate(Instant.now());
        var.setJobEngineProjectName(variableModel.getProjectName());
        // JEStringSubstitutor.addVariable(var.getJobEngineProjectID(), var.getName(),
        // (String) var.getValue());
        VariableManager.addVariable(var);
        RuleEngineHandler.addVariable(var);

    }

    // remove variable from runner
    public void deleteVariable(String projectId, String varId) {
        JELogger.debug("[projectId = " + projectId + "] [variable = " + varId + "]" + JEMessages.REMOVING_VARIABLE,
                LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, varId);
        VariableManager.removeVariable(projectId, varId);
        RuleEngineHandler.deleteVariable(projectId, varId);

    }

    public void writeVariableValue(String projectId, String variableId, String value, boolean ignoreIfSameValue) throws VariableException, VariableNotFoundException {
        //JELogger.debug("[projectId = " + projectId + "] [variable = " + variableId + "]" + JEMessages.UPDATING_VARIABLE,
        //	LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
        JEVariable var = VariableManager.updateVariableValue(projectId, variableId, value, ignoreIfSameValue);
        if (var != null) {
            RuleEngineHandler.addVariable(var);
        }

    }

    public void addJarToProject(HashMap<String, String> payload) {
        JELogger.debug(ADDING_JAR_FILE_TO_RUNNER + payload, LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
        // TODO finish this once the ui specs are decided
        try {
            //JarFile j = new JarFile(payload.get("path"));
            //JobEngine.addJarFile(payload.get("name"), j);

            //JELogger.debug("hello There, your uploaded file is " + JobEngine.getJarFile("org.eclipse.jdt.core-3.7.1.jar").getName());
            JELogger.control("Jar file uploaded successfully", LogCategory.RUNTIME, "", LogSubModule.CLASS, "");
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }

    public void runProjectRules(String projectId)
            throws RulesNotFiredException, RuleBuildFailedException {

        projectStatus.put(projectId, true);

        RuleEngineHandler.startProjectRuleEngine(projectId);

        for (JEVariable variable : VariableManager.getAllVariables(projectId)) {
            RuleEngineHandler.addVariable(variable);
        }

    }

    public void runRuleEngine(String projectId) throws RulesNotFiredException, RuleBuildFailedException {

        projectStatus.put(projectId, true);

        RuleEngineHandler.startProjectRuleEngine(projectId);

        for (JEVariable variable : VariableManager.getAllVariables(projectId)) {
            RuleEngineHandler.addVariable(variable);
        }

    }

    public void shutDownRuleEngine(String projectId) {

        RuleEngineHandler.stopProjectRuleEngine(projectId);

        projectStatus.put(projectId, false);

    }

    public JEVariable getVariable(String projectId, String variableId) throws VariableNotFoundException {

        return VariableManager.getVariableValue(projectId, variableId);

    }

}
