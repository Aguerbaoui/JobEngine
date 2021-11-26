package io.je.runtime.services;

import io.je.JEProcess;
import io.je.project.variables.VariableManager;
import io.je.runtime.beans.DMListener;
import io.je.runtime.data.DataModelListener;
import io.je.runtime.events.EventManager;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RunnerRuleModel;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.serviceTasks.*;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.classloader.JEClassCompiler;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.execution.JobEngine;
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.*;
import io.je.utilities.ruleutils.OperationStatusDetails;
import io.je.utilities.runtimeobject.JEObject;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;
import java.util.jar.JarFile;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;

/*
 * Service class to handle JERunner inputs
 */
@Service
public class RuntimeDispatcher {

	//

	// projects
	static Map<String, Boolean> projectStatus = new HashMap<>(); // key: projectId , value : true if project is running,
																	// false if not

	///////////////////////////////// PROJECT
	// build project
	public void buildProject(String projectId) throws RuleBuildFailedException, WorkflowBuildException {
		/*JELogger.debug("[projectId  = " + projectId + "]" + JEMessages.BUILDING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);*/
		RuleEngineHandler.buildProject(projectId);
		WorkflowEngineHandler.buildProject(projectId);
	}

	// run project
	public void runProject(String projectId) throws JEException {

		projectStatus.put(projectId, true);
		List<String> topics = DataModelListener.getTopicsByProjectId(projectId);

		JELogger.debugWithoutPublish("[projectId  = " + projectId + "]" + JEMessages.RUNNING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);
		try {
			// start listening to datasources
			DataModelListener.startListening(topics);

			// reset variables TODO: make it configurable//Same for events
			VariableManager.resetVariableValues(projectId);

			// run workflows
			WorkflowEngineHandler.runAllWorkflows(projectId, true);
			RuleEngineHandler.runRuleEngineProject(projectId);
			for (JEVariable variable : VariableManager.getAllVariables(projectId)) {
				RuleEngineHandler.addVariable(variable);
				RuleEngineHandler.addVariable(variable);

			}
		} catch (JEException e) {
			JELogger.error(" [projectId  = " + projectId + "]" + JEMessages.PROJECT_RUN_FAILED, LogCategory.RUNTIME,
					projectId, LogSubModule.JERUNNER, null);
			DataModelListener.stopListening(topics);
			RuleEngineHandler.stopRuleEngineProjectExecution(projectId);
			WorkflowEngineHandler.stopProjectWorfklows(projectId);
			projectStatus.put(projectId, false);
			throw e;
		}

	}

	// stop project
	// run project
	public void stopProject(String projectId) {

		// stop workflows
		JELogger.debug("[projectId  = " + projectId + "]" + JEMessages.STOPPING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);
		WorkflowEngineHandler.stopProjectWorfklows(projectId);
		List<String> topics = DataModelListener.getTopicsByProjectId(projectId);
		DataModelListener.stopListening(topics);
		projectStatus.put(projectId, false);

	}

	/*
	 * private int numberOfActiveProjectsByTopic(String topic) { int counter = 0;
	 * Set<String> projects = projectsByTopic.get(topic); for (String projectId :
	 * projects) { if (Boolean.TRUE.equals(projectStatus.get(projectId))) {
	 * counter++; } }
	 * 
	 * return counter; }
	 */
	// ***********************************RULES********************************************************

	// add rule
	public void addRule(RunnerRuleModel runnerRuleModel) throws RuleAlreadyExistsException, RuleCompilationException,
			RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException {

		JELogger.debug(JEMessages.ADDING_RULE + " : " + runnerRuleModel.getRuleName(), LogCategory.RUNTIME,
				runnerRuleModel.getProjectId(), LogSubModule.RULE, runnerRuleModel.getRuleId());
		RuleEngineHandler.addRule(runnerRuleModel);
	}

	// update rule
	public void updateRule(RunnerRuleModel runnerRuleModel)
			throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {
		JELogger.debug(JEMessages.UPDATING_RULE + " : " + runnerRuleModel.getRuleId(), LogCategory.RUNTIME,
				runnerRuleModel.getProjectId(), LogSubModule.RULE, runnerRuleModel.getRuleId());
		List<String> topics = DataModelListener.getRuleTopicsByProjectId(runnerRuleModel.getProjectId());

		// start listening to datasources
		DataModelListener.startListening(topics);
		RuleEngineHandler.updateRule(runnerRuleModel);

	}

	// compile rule
	public void compileRule(RunnerRuleModel runnerRuleModel)
			throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
		JELogger.debug(JEMessages.COMPILING_RULE + " : " + runnerRuleModel.getRuleName(), LogCategory.RUNTIME,
				runnerRuleModel.getProjectId(), LogSubModule.RULE, runnerRuleModel.getRuleId());
		RuleEngineHandler.compileRule(runnerRuleModel);
	}

	// delete rule
	public void deleteRule(String projectId, String ruleId) throws DeleteRuleException {
		JELogger.debug("[projectId = " + projectId + "] [ruleId = " + ruleId + "]" + JEMessages.DELETING_RULE,
				LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId);
		RuleEngineHandler.deleteRule(projectId, ruleId);
	}

	// ***********************************WORKFLOW********************************************************
	/*
	 * Add a workflow to the engine
	 */
	public void addWorkflow(WorkflowModel wf) {
		JELogger.debug("[projectId = " + wf.getProjectId() + "] [workflow = " + wf.getId() + "]" + JEMessages.ADDING_WF,
				LogCategory.RUNTIME, wf.getProjectId(), LogSubModule.WORKFLOW, wf.getId());
		JEProcess process = new JEProcess(wf.getId(), wf.getName(), wf.getPath(), wf.getProjectId(),
				wf.isTriggeredByEvent());
		process.setOnProjectBoot(wf.isOnProjectBoot());
		if (wf.isTriggeredByEvent()) {
			process.setTriggerMessage(wf.getTriggerMessage());
		}
		for (TaskModel task : wf.getTasks()) {
			ActivitiTask activitiTask = WorkflowEngineHandler.parseTask(wf.getProjectId(), wf.getId(), task);
			ActivitiTaskManager.addTask(activitiTask);
			process.addActivitiTask(activitiTask);
		}
		if(wf.getEndBlockEventId() != null) {
			process.setEndEventId(wf.getEndBlockEventId());
		}
		WorkflowEngineHandler.addProcess(process);

	}

	/*
	 * Launch a workflow without variables
	 */
	public void launchProcessWithoutVariables(String projectId, String key, boolean runProject)
			throws WorkflowNotFoundException,  WorkflowAlreadyRunningException,
			WorkflowBuildException, WorkflowRunException {
		JELogger.debug("[projectId = " + projectId + "] [workflow = " + key + "]" + JEMessages.RUNNING_WF,
				LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, key);
		buildWorkflow(projectId, key);
		WorkflowEngineHandler.launchProcessWithoutVariables(projectId, key, runProject);

	}

	/*
	 * Run all workflows deployed in the engine without project specification
	 */
	public void runAllWorkflows(String projectId) throws WorkflowNotFoundException, WorkflowBuildException {
		JELogger.debug("[projectId = " + projectId + "]" + JEMessages.RUNNING_WFS, LogCategory.RUNTIME, projectId,
				LogSubModule.WORKFLOW, null);
		WorkflowEngineHandler.runAllWorkflows(projectId, false);
	}

	/*
	 * Deploy a workflow to the engine
	 */
	public void buildWorkflow(String projectId, String key) throws WorkflowBuildException {
		JELogger.debug("[projectId = " + projectId + "] [workflow = " + key + "]" + JEMessages.DEPLOYING_WF,
				LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, key);
		WorkflowEngineHandler.deployBPMN(projectId, key);
	}

	///////////////////////////// Classes
	// add class
	public void addClass(ClassModel classModel) throws ClassLoadException {
		JELogger.debug(JEMessages.ADDING_CLASS+": "+classModel.getClassName(), LogCategory.RUNTIME, null, LogSubModule.CLASS, null);
		//if (!ClassRepository.containsClass(classModel.getClassId())) {
			JEClassCompiler.compileClass(classModel.getClassPath(), ConfigurationConstants.RUNNER_CLASS_LOAD_PATH);
			try {
				
				Class<?> c = JEClassLoader.getInstance()
						.loadClass(ClassBuilderConfig.generationPackageName + "." + classModel.getClassName());
				ClassRepository.addClass(classModel.getClassId(), classModel.getClassName(), c);
			/*	JELogger.debug(" ! "+JEClassLoader.getInstance().toString());
				JELogger.debug(" ! "+c.getClassLoader().toString());
				JELogger.debug(" ! "+ClassRepository.getClassById(classModel.getClassId()).getClassLoader().toString());
*/
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ClassLoadException(
						"[class :" + classModel.getClassName() + " ]" + JEMessages.CLASS_LOAD_FAILED);
			}
		/*}else {
			JELogger.warn(JEMessages.FAILED_TO_LOAD_CLASS +" : "+ JEMessages.CLASS_ALREADY_EXISTS,
	                LogCategory.RUNTIME, null,
	                LogSubModule.CLASS, null);
		}*/

	}

	public void updateClass(ClassModel classModel) throws ClassLoadException, ClassNotFoundException {
		JEClassLoader.overrideInstance(ClassBuilderConfig.generationPackageName + "." + classModel.getClassName());
		addClass(classModel);
		RuleEngineHandler.reloadContainers();
		//JELogger.debug(">>>>> "+JEClassLoader.getInstance().toString());

	}

	public void updateClasses(List<ClassModel> classes) throws ClassLoadException {
		for (ClassModel classModel : classes) {
			addClass(classModel);
		}
	}

	// update class
	// delete class

	public static void injectData(JEData jeData) throws InstanceCreationFailed {
		JELogger.trace(JEMessages.INJECTING_DATA, LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
		try {
			JEObject instanceData = InstanceManager.createInstance(jeData.getData());
			for (String projectId : DataModelListener.getProjectsSubscribedToTopic(jeData.getTopic())) {
				if (Boolean.TRUE.equals(projectStatus.get(projectId))) {
					RuleEngineHandler.injectData(projectId, instanceData);
				}
			}
		} catch (Exception e) {
			JELogger.error(JEMessages.FAILED_TO_INJECT_DATA + e.getMessage(), LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
		}

	}

	/*
	 * add a topic
	 */
	public void addTopics(String projectId, String listenerId, String listenerType, List<String> topics) {
		if (topics != null && !topics.isEmpty()) {
			DMListener dMListener = new DMListener(listenerId, projectId, listenerType);
			DataModelListener.addDMListener(dMListener, topics);
		}
	}

	// Trigger an event
	public void triggerEvent(String projectId, String id) throws EventException, ProjectNotFoundException {
		JELogger.debug("[projectId = " + projectId + "] [event = " + id + "]" + JEMessages.EVENT_TRIGGERED,
				LogCategory.RUNTIME, projectId, LogSubModule.EVENT, id);
		EventManager.triggerEvent(projectId, id);
	}

	// Add an event to the runner
	public void addEvent(EventModel eventModel) {
		JEEvent e = new JEEvent(eventModel.getEventId(), eventModel.getProjectId(), eventModel.getName(),
				EventType.valueOf(eventModel.getEventType()), eventModel.getDescription(), eventModel.getTimeout(),
				eventModel.getTimeoutUnit(), eventModel.getCreatedBy(), eventModel.getModifiedBy());

		JELogger.debug(
				"[projectId = " + e.getJobEngineProjectID() + "] [event = " + e.getJobEngineElementID() + "]"
						+ JEMessages.ADDING_EVENT,
				LogCategory.RUNTIME, eventModel.getProjectId(), LogSubModule.EVENT, eventModel.getEventId());

		EventManager.addEvent(eventModel.getProjectId(), e);
	}

	public void updateEventType(String projectId, String eventId, String eventType)
			throws ProjectNotFoundException, EventException {
		JELogger.debug("[projectId = " + projectId + "] [event = " + eventId + "]" + JEMessages.UPDATING_EVENT
				+ " to type = " + eventType, LogCategory.RUNTIME, projectId, LogSubModule.EVENT, eventId);
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
		JELogger.debug("[projectId = " + projectId + "]" + JEMessages.DELETING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);
		EventManager.deleteProjectEvents(projectId);
		WorkflowEngineHandler.deleteProjectProcesses(projectId);
		RuleEngineHandler.deleteProjectRules(projectId);
		DataModelListener.removeDMListener(projectId);
	}

	// remove rule topics
	public void removeRuleTopics(String projectId, String ruleId) {
		DataModelListener.removeDMListener(ruleId);

	}

	// remove/stop workflow from runner
	public void removeWorkflow(String projectId, String workflowId) {
		JELogger.debug("[projectId = " + projectId + "] [workflow = " + workflowId + "]" + JEMessages.REMOVING_WF,
				LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, workflowId);

		try {
			WorkflowEngineHandler.deleteProcess(projectId, workflowId);
		} catch (WorkflowRunException e) {
			JELogger.error(JEMessages.ERROR_DELETING_A_NON_EXISTING_PROCESS,
					LogCategory.RUNTIME, projectId,
					LogSubModule.WORKFLOW, workflowId);
		}
	}

	// add variable to runner
	public void addVariable(VariableModel variableModel) {
		JELogger.debug(
				"[projectId = " + variableModel.getProjectId() + "] [variable = " + variableModel.getId() + "]"
						+ JEMessages.ADDING_VARIABLE,
				LogCategory.RUNTIME, variableModel.getProjectId(), LogSubModule.VARIABLE, variableModel.getId());
		JEVariable var = new JEVariable(variableModel.getId(), variableModel.getProjectId(), variableModel.getName(),
				variableModel.getType(), variableModel.getInitialValue(), variableModel.getDescription(),
				variableModel.getCreatedBy(), variableModel.getModifiedBy());
		var.setJeObjectCreationDate(LocalDateTime.now());
		var.setJeObjectLastUpdate(LocalDateTime.now());
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

	public void writeVariableValue(String projectId, String variableId, String value) {
		JELogger.trace("[projectId = " + projectId + "] [variable = " + variableId + "]" + JEMessages.UPDATING_VARIABLE,
				LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
		JEVariable var = VariableManager.updateVariableValue(projectId, variableId, value);
		if (var != null) {
			RuleEngineHandler.addVariable(var);
		}

	}

	public void addJarToProject(HashMap<String, String> payload) {
		JELogger.debug(ADDING_JAR_FILE_TO_RUNNER + payload, LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
		// TODO finish this once the ui specs are decided
		try {
			JarFile j = new JarFile(payload.get("path"));
			JobEngine.addJarFile(payload.get("name"), j);

			//JELogger.debug("hello There, your uploaded file is " + JobEngine.getJarFile("org.eclipse.jdt.core-3.7.1.jar").getName());
			JELogger.info("Jar file uploaded successfully", LogCategory.RUNTIME, "", LogSubModule.CLASS, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runProjectRules(String projectId)
			throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {

		List<String> topics = DataModelListener.getRuleTopicsByProjectId(projectId);
		DataModelListener.startListening(topics);
		//RuleEngineHandler.buildProject(projectId);
		RuleEngineHandler.runRuleEngineProject(projectId);
		projectStatus.put(projectId, true);

	}

	public void shutDownRuleEngine(String projectId) {


		RuleEngineHandler.stopRuleEngineProjectExecution(projectId);
		projectStatus.put(projectId, false);

	}

	public List<OperationStatusDetails> updateRules(List<RunnerRuleModel> runnerRuleModels)  {
		
		List<OperationStatusDetails> updateResult = new ArrayList<>();
		for (RunnerRuleModel runnerRuleModel : runnerRuleModels)
		{
			OperationStatusDetails details = new OperationStatusDetails(runnerRuleModel.getRuleId());
			 removeRuleTopics(runnerRuleModel.getProjectId(), runnerRuleModel.getRuleId());
	         addTopics(runnerRuleModel.getProjectId(), runnerRuleModel.getRuleId(),"rule",runnerRuleModel.getTopics());
	         try {
				updateRule(runnerRuleModel);
				details.setOperationSucceeded(true);
			} catch (RuleCompilationException | JEFileNotFoundException | RuleFormatNotValidException e) {
				details.setOperationSucceeded(false);
				details.setOperationError(e.getMessage());
			}
		}
		return updateResult;
		
	}

	public void compileRules(List<RunnerRuleModel> runnerRuleModels) throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
		for (RunnerRuleModel runnerRuleModel : runnerRuleModels)
		{
	         compileRule(runnerRuleModel);
		}
		
	}

}
