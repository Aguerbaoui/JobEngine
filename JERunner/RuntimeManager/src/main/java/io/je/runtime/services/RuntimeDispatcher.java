package io.je.runtime.services;

import io.je.JEProcess;
import io.je.runtime.data.DataListener;
import io.je.runtime.events.EventManager;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.repos.ClassRepository;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.serviceTasks.ActivitiTaskManager;
import io.je.serviceTasks.ScriptTask;
import io.je.serviceTasks.WebApiTask;
import io.je.utilities.apis.BodyType;
import io.je.utilities.apis.HttpMethod;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventModel;
import io.je.utilities.models.EventType;
import io.je.utilities.models.TaskModel;
import io.je.utilities.models.WorkflowModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

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
    public void buildProject(String projectId) throws RuleBuildFailedException {

        JELogger.trace(" Building rules and workflows for project id = " + projectId);
        RuleEngineHandler.buildProject(projectId);
        WorkflowEngineHandler.buildProject(projectId);


    }

    // run project
    public void runProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException,
            ProjectAlreadyRunningException, WorkflowNotFoundException {


        projectStatus.put(projectId, true);
        ArrayList<String> topics = new ArrayList<>();
        // get topics :
        for (Entry<String, Set<String>> entry : projectsByTopic.entrySet()) {
            if (entry.getValue().contains(projectId)) {
                topics.add(entry.getKey());
            }

        }
        JELogger.trace(" Running rules,workflows and data listener for project id = " + projectId);
        try
        {
        	DataListener.startListening(topics);
            RuleEngineHandler.runRuleEngineProject(projectId);
            WorkflowEngineHandler.runAllWorkflows(projectId);
        }catch (JEException e) {
            JELogger.warning(getClass()," Failed to run project id = " + projectId);
			DataListener.stopListening(topics);
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
        JELogger.trace(" Stopping rules,workflows and data listener for project id = " + projectId);
        WorkflowEngineHandler.stopProjectWorfklows(projectId);
        RuleEngineHandler.stopRuleEngineProjectExecution(projectId);

        ArrayList<String> topics = new ArrayList<>();
        // get topics :
        for (Entry<String, Set<String>> entry : projectsByTopic.entrySet()) {
            //if more than 1 active project is listening on that topic we dont stop the thread
            if (entry.getValue().contains(projectId) && numberOfActiveProjectsByTopic(entry.getKey())==1) {
                topics.add(entry.getKey());
            }

        }
        DataListener.stopListening(topics);
        projectStatus.put(projectId, false);

    }
    
    private int numberOfActiveProjectsByTopic(String topic)
    {
    	int counter = 0;
    	Set<String> projects = projectsByTopic.get(topic);
    	for(String projectId: projects)
    	{
    		if(Boolean.TRUE.equals(projectStatus.get(projectId)))
    		{
    			counter++;
    		}
    	}
    	
    	
    	return counter;
    }

    // ***********************************RULES********************************************************


    // add rule
    public void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException,
            RuleNotAddedException, JEFileNotFoundException, RuleFormatNotValidException {

        JELogger.trace("adding rule : " + ruleModel.getRuleName());
        RuleEngineHandler.addRule(ruleModel);
    }

    // update rule
    public void updateRule(RuleModel ruleModel)
            throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {
        JELogger.trace("updating rule : " + ruleModel.getRuleId());
        RuleEngineHandler.updateRule(ruleModel);

    }

    // compile rule
    public void compileRule(RuleModel ruleModel)
            throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
        JELogger.trace("Compiling rule : " + ruleModel.getRuleId());
        RuleEngineHandler.compileRule(ruleModel);
    }

    // delete rule
    public void deleteRule(String projectId, String ruleId) throws DeleteRuleException {
        JELogger.trace("Deleting rule id = " + ruleId + " in project id = " + projectId);
        RuleEngineHandler.deleteRule(projectId, ruleId);
    }

    // ***********************************WORKFLOW********************************************************
    /*
     * Add a workflow to the engine
     */
    public void addWorkflow(WorkflowModel wf) {
        JELogger.trace(" Adding workflow to engine with key = " + wf.getKey() + " in project id = " + wf.getProjectId());
        JEProcess process = new JEProcess(wf.getKey(), wf.getName(), wf.getPath(), wf.getProjectId(), wf.isTriggeredByEvent());
        for(TaskModel task: wf.getTasks()) {
            if(task.getType().equals(WorkflowConstants.WEBSERVICETASK_TYPE)) {
                WebApiTask webApiTask = new WebApiTask();
                webApiTask.setBodyType(BodyType.JSON);
                webApiTask.setTaskId(task.getTaskId());
                webApiTask.setTaskName(task.getTaskName());
                webApiTask.setProcessId(wf.getKey());
                HashMap<String, Object> attributes = task.getAttributes();
                if(attributes.get("inputs") != null) {
                    webApiTask.setHasBody(true);
                    webApiTask.setBody((HashMap<String, String>) attributes.get("inputs"));
                }
                webApiTask.setHttpMethod(HttpMethod.valueOf((String) attributes.get("method")));
                webApiTask.setUrl((String) attributes.get("url"));
                process.addActivitiTask(webApiTask);
                ActivitiTaskManager.addTask(webApiTask);
            }

            if(task.getType().equals(WorkflowConstants.SCRIPTTASK_TYPE)) {
                ScriptTask scriptTask = new ScriptTask();
                scriptTask.setTaskName(task.getTaskName());
                scriptTask.setTaskId(task.getTaskId());
                HashMap<String, Object> attributes = task.getAttributes();
                if(attributes.get("script") != null) {
                    scriptTask.setScript((String) attributes.get("script"));
                }
                JEClassLoader.generateScriptTaskClass(scriptTask.getTaskName(), scriptTask.getScript());
                process.addActivitiTask(scriptTask);
                ActivitiTaskManager.addTask(scriptTask);
            }
        }
        WorkflowEngineHandler.addProcess(process);

    }

    /*
     * Launch a workflow without variables
     */
    public void launchProcessWithoutVariables(String projectId, String key) throws WorkflowNotFoundException, WorkflwTriggeredByEventException, WorkflowAlreadyRunningException {
        try {
            JELogger.trace(" Running workflow with key = " + key + " in project id = " + projectId);
            WorkflowEngineHandler.launchProcessWithoutVariables(projectId, key);
        } catch (WorkflowAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    /*
     * Run all workflows deployed in the engine without project specification
     */
    public void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
        JELogger.trace(" Running all workflows in project id = " + projectId);
        WorkflowEngineHandler.runAllWorkflows(projectId);
    }

    /*
     * Deploy a workflow to the engine
     */
    public void buildWorkflow(String projectId, String key) {
        JELogger.trace(" Deploying workflow with key = " + key + " in project id = " + projectId);
        WorkflowEngineHandler.deployBPMN(projectId, key);
    }

    ///////////////////////////// Classes
    // add class
    public void addClass(ClassModel classModel) throws ClassLoadException {
        JELogger.trace(" Adding class to runner, class name =  " + classModel.getClassName());
       JEClassLoader.loadClass(classModel.getClassPath(), ConfigurationConstants.runnerClassLoadPath);
       try {
    	   ClassRepository.addClass(classModel.getClassId(), RuntimeDispatcher.class.getClassLoader().loadClass(ClassBuilderConfig.genrationPackageName + "." + classModel.getClassName())); ;
	} catch (ClassNotFoundException e) {
		throw new ClassLoadException("Failed to load class to runner. [class :"+ classModel.getClassName() +" ]"); 
	}
        

    }

    // update class
    // delete class


    public static void injectData(JEData jeData) throws InstanceCreationFailed {
        JELogger.debug(" Injecting data in runner from listener");
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
        JELogger.trace("adding topics : " + topics);
        if (topics != null) {
            for (String topic : topics) {
                if (!projectsByTopic.containsKey(topic)) {
                    projectsByTopic.put(topic, new HashSet<>());
                }
                if (!projectsByTopic.get(topic).contains(projectId)) {
                    projectsByTopic.get(topic).add(projectId);
                    DataListener.subscribeToTopic(topic);
                } else {
                    DataListener.incrementSubscriptionCount(topic);
                }

            }
        }
    }

    public void triggerEvent(String projectId, String id) throws EventException, ProjectNotFoundException {
        JELogger.trace(" Triggering event id = " + id + " in project id = " + projectId);
        EventManager.triggerEvent(projectId, id);
    }


    public void addEvent(EventModel eventModel) {
        JEEvent e = new JEEvent();
        e.setName(eventModel.getName());
        e.setTriggeredById(eventModel.getEventId());
        e.setJobEngineElementID(eventModel.getEventId());
        e.setJobEngineProjectID(eventModel.getProjectId());
        e.setType(EventType.valueOf(eventModel.getEventType()));
        JELogger.trace(" Adding event with id = " + e.getJobEngineElementID());
        EventManager.addEvent(eventModel.getProjectId(), e);
    }


    public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException {
        JELogger.trace(" updating event id = " + eventId + " in project id = " + projectId + " to type = " + eventType);
        EventManager.updateEventType(projectId, eventId, eventType);
    }

    public void deleteEvent(String projectId, String eventId) throws ProjectNotFoundException, EventException {
        JELogger.trace(" deleting event id = " + eventId + " in project id = " + projectId);
        EventManager.deleteEvent(projectId, eventId);
    }

    //clean project data from runner
    //Remove events, topics to listen to, rules and workflows
    public void removeProjectData(String projectId) {
        JELogger.trace(" deleting project data id = " + projectId );
        EventManager.deleteProjectEvents(projectId);
        WorkflowEngineHandler.deleteProjectProcesses(projectId);
        RuleEngineHandler.deleteProjectRules(projectId);
        decrementTopicSubscriptionCount(projectId);
    }

    //decrement topic subscription count for a project
    public void decrementTopicSubscriptionCount(String projectId) {
        JELogger.trace(" Removing topic subscription in project id = " + projectId);
        for (String topic : projectsByTopic.keySet()) {
            HashSet<String> set = (HashSet<String>) projectsByTopic.get(topic);
            for (String id : set) {
                if (id.equalsIgnoreCase(projectId)) {
                    DataListener.decrementSubscriptionCount(topic);
                }
            }
        }
    }

	public void removeRuleTopics(String projectId, String ruleId) {
		ArrayList<String> oldTopics =  (ArrayList<String>) RuleEngineHandler.getRuleTopics(projectId,ruleId);
        JELogger.debug(getClass(),"old rule topics : " + oldTopics);

		for(String topic : oldTopics)
		{
			DataListener.decrementSubscriptionCount(topic);
		}
		
	}

    public void removeWorkflow(String projectId, String workflowId) {
        JELogger.info("Removing workflow from runner with id = " + workflowId + " in project id = " + projectId);
        WorkflowEngineHandler.deleteProcess(projectId,workflowId);
    }
}
