package io.je.runtime.services;

import io.je.runtime.events.EventManager;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.beans.EventTriggeredCallback;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.exceptions.*;
import io.je.utilities.models.EventModel;
import io.je.utilities.models.WorkflowModel;
import org.springframework.stereotype.Service;

import io.je.runtime.data.DataListener;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.InstanceModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.objects.ClassManager;
import io.je.runtime.objects.InstanceManager;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DeleteRuleException;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.exceptions.JEFileNotFoundException;
import io.je.utilities.exceptions.ProjectAlreadyRunningException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleFormatNotValidException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.exceptions.RulesNotFiredException;
import io.je.utilities.logger.JELogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * Service class to handle JERunner inputs
 */
@Service
public class RuntimeDispatcher {

	//
	static Map<String, Set<String>> projectsByTopic = new HashMap<>(); // key : topic, value: list of projects																							// of projects
	static Map<String, Boolean> projectStatus = new HashMap<>(); //key: project id, value : true if project is running, false if not



	///////////////////////////////// PROJECT
	// build project
	public void buildProject(String projectId) throws RuleBuildFailedException
	{

		RuleEngineHandler.buildProject(projectId);
		WorkflowEngineHandler.buildProject(projectId);
		
		
		
	}

	// run project
	public void runProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException,
			ProjectAlreadyRunningException, WorkflowNotFoundException {
		

		/* TODO: to delete : hardcoded for test */
		String testTopic = "00fd4e5d-5f19-4b8a-9c89-66e05be497b4";
		if(projectsByTopic.get(testTopic)==null)
		{
	        projectsByTopic.put(testTopic,new HashSet<>() );

		}
		projectsByTopic.get(testTopic).add(projectId);
        
        DataListener.subscribeToTopic(testTopic);
     

       /* ------------------------------ */
		
       projectStatus.put(projectId, true);
		ArrayList<String> topics = new ArrayList<>();
		// get topics :
		for (Entry<String, Set<String>> entry : projectsByTopic.entrySet()) {
			if (entry.getValue().contains(projectId)) {
				topics.add(entry.getKey());
			}

		}

		//DataListener.startListening(topics);
		RuleEngineHandler.runRuleEngineProject(projectId);
		WorkflowEngineHandler.runAllWorkflows(projectId);
	}

	// stop project
	// run project
	public void stopProject(String projectId)
			throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
		
		// stop workflows
		
		WorkflowEngineHandler.stopProjectWorfklows(projectId);
		RuleEngineHandler.stopRuleEngineProjectExecution(projectId);

		ArrayList<String> topics = new ArrayList<>();
		// get topics :
		for (Entry<String, Set<String>> entry : projectsByTopic.entrySet()) {
			//if more than 1 project is listening on that topic we dont stop the thread
			if (entry.getValue().size() == 1 && entry.getValue().contains(projectId)) {
				topics.add(entry.getKey());
			}

		}
		DataListener.stopListening(topics);
        projectStatus.put(projectId, false);

	}

	// ***********************************RULES********************************************************


	// add rule
	public void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException,
			RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException {



		RuleEngineHandler.addRule(ruleModel);
		//TODO remove this after testing, temporary should be relocated to ruleModel
		ArrayList<EventModel> l = new ArrayList<>();
		EventModel m = new EventModel();
		m.setProjectId(ruleModel.getProjectId());
		m.setReference("msgStart");
		m.setName("msgStart");
		m.setEventId("testId");
		m.setType(JEEvent.START_WORKFLOW);
		l.add(m);
		registerEvents("1212", ruleModel.getProjectId(), l);

	}

	// update rule
	public void updateRule(RuleModel ruleModel)
			throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

		RuleEngineHandler.updateRule(ruleModel);
		//TODO remove this after testing, temporary should be relocated to ruleModel
		ArrayList<EventModel> l = new ArrayList<>();
		EventModel m = new EventModel();
		m.setProjectId(ruleModel.getProjectId());
		m.setReference("msgStart");
		m.setName("msgStart");
		m.setEventId("testId");
		m.setType(JEEvent.START_WORKFLOW);
		l.add(m);
		registerEvents("1212", ruleModel.getProjectId(), l);

	}

	// compile rule
	public void compileRule(RuleModel ruleModel)
			throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
		RuleEngineHandler.compileRule(ruleModel);
	}

	// delete rule
	public void deleteRule(String projectId, String ruleId) throws DeleteRuleException {
		RuleEngineHandler.deleteRule(projectId, ruleId);
	}

	// ***********************************WORKFLOW********************************************************
	/*
	 * Add a workflow to the engine
	 */
	public void addWorkflow(WorkflowModel wf) {
		WorkflowEngineHandler.addProcess(wf.getKey(), wf.getName(), wf.getPath(), wf.getProjectId(), wf.isTriggeredByEvent());
	}

	/*
	 * Launch a workflow without variables
	 */
	public void launchProcessWithoutVariables(String projectId, String key) throws WorkflowNotFoundException {
		try {
			WorkflowEngineHandler.launchProcessWithoutVariables(projectId, key);
		} catch (WorkflowAlreadyRunningException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Run all workflows deployed in the engine without project specification
	 */
	public void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
		WorkflowEngineHandler.runAllWorkflows(projectId);
	}

	/*
	 * Deploy a workflow to the engine
	 */
	public void buildWorkflow(String projectId, String key) {
		WorkflowEngineHandler.deployBPMN(projectId, key);
	}

	/*
	*  Start workflow with message event
	* */
	/*public static void startProcessInstanceByMessage(String messageEvent) {
		EventManager.startProcessInstanceByMessage(messageEvent);
	}*/

	/*
	 *  Throw message event for workflow execution
	 * */
	/*public static void throwMessageEventInWorkflow(String messageEvent) {
		EventManager.throwMessageEventInWorkflow(messageEvent);
	}*/

	/*
	* Throw signal event for workflow execution
	* */
	/*public static void throwSignalEventInWorkflow(String messageEvent) {
		EventManager.throwSignalEventInWorkflow(messageEvent);
	}*/
	///////////////////////////// Classes
	// add class
	public void addClass(ClassModel classModel) throws ClassLoadException {

		ClassManager.loadClass(classModel.getClassId(), classModel.getClassName(), classModel.getClassPath());

	}

	// update class
	// delete class

	/////////////////////////////// instance creation example : TODO to be deleted 
	public void addInstanceTest(InstanceModel instanceModel) throws InstanceCreationFailed {
		JELogger.info(getClass(), instanceModel.toString());
		InstanceManager.createInstance(instanceModel);
	}

	public static void injectData(JEData jeData) throws InstanceCreationFailed {
		for (String projectId : projectsByTopic.get(jeData.getTopic())) {
			if (Boolean.TRUE.equals(projectStatus.get(projectId))) {
				RuleEngineHandler.injectData(projectId, jeData);
			}
		}

	}

	/*
	 * add a topic 
	 */
	public void addTopics(String projectId, List<String> topics) {
		if(topics != null)
		{
			for(String topic: topics) {
				if(!projectsByTopic.containsKey(topic)) {
					projectsByTopic.put(topic, new HashSet<>());				
				}
				if(!projectsByTopic.get(topic).contains(projectId))
				{
					projectsByTopic.get(topic).add(projectId);
					DataListener.subscribeToTopic(topic);
				}
				
			}
		}
	}

	public void triggerEvent(String projectId, String id) {
		EventManager.triggerEvent(projectId, id);
	}


	public void registerEvents(String id, String projectId, ArrayList<EventModel> events) {
		for(EventModel event: events) {
			JEEvent e = new JEEvent();
			e.setName(event.getName());
			e.setTriggeredById(id);
			e.setReference(event.getReference());
			e.setJobEngineElementID(event.getEventId());
			e.setJobEngineProjectID(projectId);
			e.setType(event.getType());
			e.setTriggeredCallback(eventId -> triggerEvent(projectId, event.getReference()));
			//RuleEngineHandler.addRuleEvent(id, projectId, e);
			EventManager.addEvent(projectId, e);
		}
	}
}
