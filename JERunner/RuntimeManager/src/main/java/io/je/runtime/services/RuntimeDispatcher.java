package io.je.runtime.services;

import io.je.JEProcess;
import io.je.project.variables.VariableManager;
import io.je.runtime.data.DataModelListener;
import io.je.runtime.events.EventManager;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RuleModel;
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
import io.je.utilities.instances.ClassRepository;
import io.je.utilities.instances.InstanceManager;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.mapping.InstanceModelMapping;
import io.je.utilities.models.*;
import io.je.utilities.runtimeobject.JEObject;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarFile;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;

/*
 * Service class to handle JERunner inputs
 */
@Service
public class RuntimeDispatcher {

	//
	static Map<String, Set<String>> projectsByTopic = new HashMap<>(); // key : topic, value: list of projects // of
																		// projects
	static Map<String, Boolean> projectStatus = new HashMap<>(); // key: projectId , value : true if project is running,
																	// false if not
	public static ObjectMapper objectMapper = new ObjectMapper();

	///////////////////////////////// PROJECT
	// build project
	public void buildProject(String projectId) throws RuleBuildFailedException, WorkflowBuildException {
		JELogger.debug("[projectId  = " + projectId + "]" + JEMessages.BUILDING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);
		RuleEngineHandler.buildProject(projectId);
		WorkflowEngineHandler.buildProject(projectId);

	}

	// run project
	public void runProject(String projectId) throws JEException {

		projectStatus.put(projectId, true);
		ArrayList<String> topics = new ArrayList<>();
		// get topics :
		for (Entry<String, Set<String>> entry : projectsByTopic.entrySet()) {
			if (entry.getValue().contains(projectId)) {
				topics.add(entry.getKey());
			}

		}
		JELogger.debug("[projectId  = " + projectId + "]" + JEMessages.RUNNING_PROJECT, LogCategory.RUNTIME, projectId,
				LogSubModule.JERUNNER, null);
		try {
			// start listening to datasources
			DataModelListener.startListening(topics);

			// reset variables TODO: make it configurable//Same for events
			VariableManager.resetVariableValues(projectId);
			for (JEVariable variable : VariableManager.getAllVariables(projectId)) {
				RuleEngineHandler.addVariable(variable);
			}

			// run workflows
			WorkflowEngineHandler.runAllWorkflows(projectId);
			RuleEngineHandler.runRuleEngineProject(projectId);

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
		RuleEngineHandler.stopRuleEngineProjectExecution(projectId);

		ArrayList<String> topics = new ArrayList<>();
		// get topics :
		for (Entry<String, Set<String>> entry : projectsByTopic.entrySet()) {
			// if more than 1 active project is listening on that topic we dont stop the
			// thread
			if (entry.getValue().contains(projectId) && numberOfActiveProjectsByTopic(entry.getKey()) == 1) {
				topics.add(entry.getKey());
			}

		}
		DataModelListener.stopListening(topics);
		projectStatus.put(projectId, false);

	}

	private int numberOfActiveProjectsByTopic(String topic) {
		int counter = 0;
		Set<String> projects = projectsByTopic.get(topic);
		for (String projectId : projects) {
			if (Boolean.TRUE.equals(projectStatus.get(projectId))) {
				counter++;
			}
		}

		return counter;
	}

	// ***********************************RULES********************************************************

	// add rule
	public void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException,
			RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException {

		JELogger.debug(JEMessages.ADDING_RULE + " : " + ruleModel.getRuleName(), LogCategory.RUNTIME,
				ruleModel.getProjectId(), LogSubModule.RULE, ruleModel.getRuleId());
		RuleEngineHandler.addRule(ruleModel);
	}

	// update rule
	public void updateRule(RuleModel ruleModel)
			throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {
		JELogger.debug(JEMessages.UPDATING_RULE + " : " + ruleModel.getRuleId(), LogCategory.RUNTIME,
				ruleModel.getProjectId(), LogSubModule.RULE, ruleModel.getRuleId());
		RuleEngineHandler.updateRule(ruleModel);

	}

	// compile rule
	public void compileRule(RuleModel ruleModel)
			throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
		JELogger.debug(JEMessages.COMPILING_RULE + " : " + ruleModel.getRuleId(), LogCategory.RUNTIME,
				ruleModel.getProjectId(), LogSubModule.RULE, ruleModel.getRuleId());
		RuleEngineHandler.compileRule(ruleModel);
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
		JELogger.debug(
				"[projectId = " + wf.getProjectId() + "] [workflow = " + wf.getKey() + "]" + JEMessages.ADDING_WF,
				LogCategory.RUNTIME, wf.getProjectId(), LogSubModule.WORKFLOW, wf.getKey());
		JEProcess process = new JEProcess(wf.getKey(), wf.getName(), wf.getPath(), wf.getProjectId(),
				wf.isTriggeredByEvent());
		process.setOnProjectBoot(wf.isOnProjectBoot());
		if (wf.isTriggeredByEvent()) {
			process.setTriggerMessage(wf.getTriggerMessage());
		}
		for (TaskModel task : wf.getTasks()) {
			ActivitiTask activitiTask = WorkflowEngineHandler.parseTask(wf.getProjectId(), wf.getKey(), task);
			ActivitiTaskManager.addTask(activitiTask);
			process.addActivitiTask(activitiTask);
		}
		WorkflowEngineHandler.addProcess(process);

	}

	/*
	 * Launch a workflow without variables
	 */
	public void launchProcessWithoutVariables(String projectId, String key, boolean runProject)
			throws WorkflowNotFoundException, WorkflwTriggeredByEventException, WorkflowAlreadyRunningException,
			WorkflowBuildException {
		JELogger.debug("[projectId = " + projectId + "] [workflow = " + key + "]" + JEMessages.RUNNING_WF,
				LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, key);
		WorkflowEngineHandler.launchProcessWithoutVariables(projectId, key, runProject);

	}

	/*
	 * Run all workflows deployed in the engine without project specification
	 */
	public void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
		JELogger.debug("[projectId = " + projectId + "]" + JEMessages.RUNNING_WFS, LogCategory.RUNTIME, projectId,
				LogSubModule.WORKFLOW, null);
		WorkflowEngineHandler.runAllWorkflows(projectId);
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
		JELogger.debug(JEMessages.ADDING_CLASS, LogCategory.RUNTIME, null, LogSubModule.CLASS, null);
		if (!ClassRepository.containsClass(classModel.getClassId())) {
			JEClassCompiler.compileClass(classModel.getClassPath(), ConfigurationConstants.runnerClassLoadPath);
			try {

				ClassRepository.addClass(classModel.getClassId(), JEClassLoader.getInstance()
						.loadClass(ClassBuilderConfig.generationPackageName + "." + classModel.getClassName()));

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ClassLoadException(
						"[class :" + classModel.getClassName() + " ]" + JEMessages.CLASS_LOAD_FAILED);
			}
		}else {
			JELogger.warn(JEMessages.FAILED_TO_LOAD_CLASS +" : "+ JEMessages.CLASS_ALREADY_EXISTS,
	                LogCategory.RUNTIME, null,
	                LogSubModule.CLASS, null);
		}

	}

	public void updateClass(ClassModel classModel) throws ClassLoadException {
		JEClassLoader.overrideInstance();
		addClass(classModel);
	}

	public void updateClasses(List<ClassModel> classes) throws ClassLoadException {
		for (ClassModel classModel : classes) {
			addClass(classModel);
		}
	}

	// update class
	// delete class

	public static void injectData(JEData jeData) throws InstanceCreationFailed {
		JELogger.debug(JEMessages.INJECTING_DATA, LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
		try {
			JEObject instanceData = InstanceManager.createInstance(jeData.getData());
			for (String projectId : projectsByTopic.get(jeData.getTopic())) {
				if (Boolean.TRUE.equals(projectStatus.get(projectId))) {
					RuleEngineHandler.injectData(projectId, instanceData);
				}
			}
		} catch (Exception e) {
			JELogger.error("Failed to inject data : " + e.getMessage(), LogCategory.RUNTIME, null,
					LogSubModule.JERUNNER, null);
		}

	}

	/*
	 * add a topic
	 */
	public void addTopics(String projectId, List<String> topics) {
		JELogger.debug(JEMessages.ADDING_TOPICS + topics, LogCategory.RUNTIME, projectId, LogSubModule.JERUNNER, null);
		if (topics != null) {
			for (String topic : topics) {
				if (!projectsByTopic.containsKey(topic)) {
					projectsByTopic.put(topic, new HashSet<>());
				}
				if (!projectsByTopic.get(topic).contains(projectId)) {
					projectsByTopic.get(topic).add(projectId);
					DataModelListener.subscribeToTopic(topic);
				} else {
					DataModelListener.incrementSubscriptionCount(topic);
				}

			}
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
				eventModel.getTimeoutUnit());

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
		decrementTopicSubscriptionCount(projectId);
	}

	// decrement topic subscription count for a project
	public void decrementTopicSubscriptionCount(String projectId) {
		JELogger.debug("[projectId = " + projectId + "]" + JEMessages.REMOVING_TOPIC_SUBSCRIPTION, LogCategory.RUNTIME,
				projectId, LogSubModule.JERUNNER, null);
		for (String topic : projectsByTopic.keySet()) {
			HashSet<String> set = (HashSet<String>) projectsByTopic.get(topic);
			for (String id : set) {
				if (id.equalsIgnoreCase(projectId)) {
					DataModelListener.decrementSubscriptionCount(topic);
				}
			}
		}
	}

	// remove rule topics
	public void removeRuleTopics(String projectId, String ruleId) {
		ArrayList<String> oldTopics = (ArrayList<String>) RuleEngineHandler.getRuleTopics(projectId, ruleId);

		for (String topic : oldTopics) {
			DataModelListener.decrementSubscriptionCount(topic);
		}

	}

	// remove/stop workflow from runner
	public void removeWorkflow(String projectId, String workflowId) {
		JELogger.debug("[projectId = " + projectId + "] [workflow = " + workflowId + "]" + JEMessages.REMOVING_WF,
				LogCategory.RUNTIME, projectId, LogSubModule.WORKFLOW, workflowId);
		WorkflowEngineHandler.deleteProcess(projectId, workflowId);
	}

	// add variable to runner
	public void addVariable(VariableModel variableModel) {
		JELogger.debug(
				"[projectId = " + variableModel.getProjectId() + "] [variable = " + variableModel.getId() + "]"
						+ JEMessages.ADDING_VARIABLE,
				LogCategory.RUNTIME, variableModel.getProjectId(), LogSubModule.VARIABLE, variableModel.getId());
		JEVariable var = new JEVariable(variableModel.getId(), variableModel.getProjectId(), variableModel.getName(),
				variableModel.getType(), variableModel.getInitialValue());
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
		JELogger.debug("[projectId = " + projectId + "] [variable = " + variableId + "]" + JEMessages.UPDATING_VARIABLE,
				LogCategory.RUNTIME, projectId, LogSubModule.VARIABLE, variableId);
		JEVariable var = VariableManager.updateVariableValue(projectId, variableId, value);
		if(var!=null)
			{
				RuleEngineHandler.addVariable(var);
			}

	}

	public void addJarToProject(HashMap<String, String> payload) {
		JELogger.debug(ADDING_JAR_FILE_TO_RUNNER + payload, LogCategory.RUNTIME, null, LogSubModule.JERUNNER, null);
		// TODO finish this once the ui specs are decided
		try {
			JarFile j = new JarFile(payload.get("path"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
