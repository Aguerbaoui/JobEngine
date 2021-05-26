package io.je.runtime.services;

import io.je.JEProcess;
import io.je.runtime.data.DataListener;
import io.je.runtime.events.EventManager;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.repos.ClassRepository;
import io.je.runtime.repos.VariableManager;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.serviceTasks.*;
import io.je.utilities.apis.BodyType;
import io.je.utilities.apis.HttpMethod;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.classloader.JEClassCompiler;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.ClassBuilderConfig;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.*;
import io.je.utilities.string.JEStringSubstitutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarFile;

import static io.je.utilities.constants.JEMessages.ADDING_JAR_FILE_TO_RUNNER;
import static io.je.utilities.constants.WorkflowConstants.*;
import static io.je.utilities.constants.WorkflowConstants.USERNAME;

/*
 * Service class to handle JERunner inputs
 */
@Service
public class RuntimeDispatcher {

    //
    static Map<String, Set<String>> projectsByTopic = new HashMap<>(); // key : topic, value: list of projects																							// of projects
    static Map<String, Boolean> projectStatus = new HashMap<>(); //key: projectId , value : true if project is running, false if not


    ///////////////////////////////// PROJECT
    // build project
    public void buildProject(String projectId) throws RuleBuildFailedException {

        JELogger.trace("[projectId  = " + projectId+"]" +JEMessages.BUILDING_PROJECT);
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
        JELogger.trace("[projectId  = " + projectId+"]"+JEMessages.RUNNING_PROJECT);
        try
        {
        	DataListener.startListening(topics);
            RuleEngineHandler.runRuleEngineProject(projectId);
            WorkflowEngineHandler.runAllWorkflows(projectId);
        }catch (JEException e) {
            JELogger.warning(getClass()," [projectId  = " + projectId+"]"+JEMessages.PROJECT_RUN_FAILED);
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
        JELogger.trace("[projectId  = " + projectId+"]"+JEMessages.STOPPING_PROJECT);
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

        JELogger.trace( JEMessages.ADDING_RULE + " : " + ruleModel.getRuleName());
        RuleEngineHandler.addRule(ruleModel);
    }

    // update rule
    public void updateRule(RuleModel ruleModel)
            throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {
        JELogger.trace(JEMessages.UPDATING_RULE + " : " + ruleModel.getRuleId());
        RuleEngineHandler.updateRule(ruleModel);

    }

    // compile rule
    public void compileRule(RuleModel ruleModel)
            throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {
        JELogger.trace(JEMessages.COMPILING_RULE + " : " + ruleModel.getRuleId());
        RuleEngineHandler.compileRule(ruleModel);
    }

    // delete rule
    public void deleteRule(String projectId, String ruleId) throws DeleteRuleException {
        JELogger.trace(getClass(), "[projectId = " + projectId + "] [ruleId = " + ruleId + "]" + JEMessages.DELETING_RULE);
        RuleEngineHandler.deleteRule(projectId, ruleId);
    }

    // ***********************************WORKFLOW********************************************************
    /*
     * Add a workflow to the engine
     */
    public void addWorkflow(WorkflowModel wf) {
        JELogger.trace(getClass(), "[projectId = " + wf.getProjectId() + "] [workflow = " + wf.getKey() + "]" + JEMessages.ADDING_WF);
        JEProcess process = new JEProcess(wf.getKey(), wf.getName(), wf.getPath(), wf.getProjectId(), wf.isTriggeredByEvent());
        if(wf.isTriggeredByEvent()) {
           process.setTriggerMessage(wf.getTriggerMessage());
        }
        for(TaskModel task: wf.getTasks()) {
            if(task.getType().equals(WorkflowConstants.WEBSERVICETASK_TYPE)) {
                WebApiTask webApiTask = new WebApiTask();
                webApiTask.setBodyType(BodyType.JSON);
                webApiTask.setTaskId(task.getTaskId());
                webApiTask.setTaskName(task.getTaskName());
                webApiTask.setProcessId(wf.getKey());
                webApiTask.setProjectId(wf.getProjectId());
                HashMap<String, Object> attributes = task.getAttributes();
                if(attributes.get(INPUTS) != null) {
                    webApiTask.setHasBody(true);
                    webApiTask.setBody((HashMap<String, String>) attributes.get(INPUTS));
                }
                else {
                    webApiTask.setHasBody(true);
                    webApiTask.setStringBody((String) attributes.get(BODY));
                }
                webApiTask.setHttpMethod(HttpMethod.valueOf((String) attributes.get(METHOD)));
                webApiTask.setUrl((String) attributes.get(URL));
                process.addActivitiTask(webApiTask);
                ActivitiTaskManager.addTask(webApiTask);
            }

            if(task.getType().equals(WorkflowConstants.SCRIPTTASK_TYPE)) {
                ScriptTask scriptTask = new ScriptTask();
                scriptTask.setTaskName(task.getTaskName());
                scriptTask.setTaskId(task.getTaskId());
                scriptTask.setProjectId(wf.getProjectId());
                HashMap<String, Object> attributes = task.getAttributes();
                if(attributes.containsKey(SCRIPT)) {
                    scriptTask.setScript((String) attributes.get(SCRIPT));
                }
                //JEClassLoader.generateScriptTaskClass(scriptTask.getTaskName(), scriptTask.getScript());
                process.addActivitiTask(scriptTask);
                ActivitiTaskManager.addTask(scriptTask);
            }

            if(task.getType().equals(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
                InformTask informTask = new InformTask();
                informTask.setTaskName(task.getTaskName());
                informTask.setTaskId(task.getTaskId());
                informTask.setProjectId(wf.getProjectId());
                HashMap<String, Object> attributes = task.getAttributes();
                if(attributes.get(MESSAGE) != null) {
                    informTask.setMessage((String) attributes.get(MESSAGE));
                }
                process.addActivitiTask(informTask);
                ActivitiTaskManager.addTask(informTask);
            }

            if(task.getType().equals(WorkflowConstants.MAILSERVICETASK_TYPE)) {
                MailTask mailTask = new MailTask();
                mailTask.setTaskId(task.getTaskId());
                mailTask.setTaskName(task.getTaskName());
                mailTask.setProjectId(wf.getProjectId());
                HashMap<String, Object> attributes = task.getAttributes();
                if(attributes.containsKey(USE_DEFAULT_CREDENTIALS)) {
                    mailTask.setbUseDefaultCredentials((boolean) task.getAttributes().get(USE_DEFAULT_CREDENTIALS));
                    mailTask.setbEnableSSL((boolean) task.getAttributes().get(ENABLE_SSL));
                }
                mailTask.setiPort((Integer) task.getAttributes().get(PORT));
                mailTask.setStrSenderAddress((String) task.getAttributes().get(SENDER_ADDRESS));
                mailTask.setiSendTimeOut((Integer) task.getAttributes().get(SEND_TIME_OUT));
                mailTask.setLstRecieverAddress((List<String>) task.getAttributes().get(RECEIVER_ADDRESS));
                mailTask.setEmailMessage((HashMap<String, String>) task.getAttributes().get(EMAIL_MESSAGE));
                mailTask.setStrSMTPServer((String) task.getAttributes().get(SMTP_SERVER));
                mailTask.setStrPassword((String) task.getAttributes().get(PASSWORD));
                mailTask.setStrUserName((String) task.getAttributes().get(USERNAME));
                process.addActivitiTask(mailTask);
                ActivitiTaskManager.addTask(mailTask);
            }
        }
        WorkflowEngineHandler.addProcess(process);

    }

    /*
     * Launch a workflow without variables
     */
    public void launchProcessWithoutVariables(String projectId, String key) throws WorkflowNotFoundException, WorkflwTriggeredByEventException, WorkflowAlreadyRunningException {
        try {
            JELogger.trace(getClass(), "[projectId = " + projectId + "] [workflow = " + key + "]" + JEMessages.RUNNING_WF);
            WorkflowEngineHandler.launchProcessWithoutVariables(projectId, key);
        } catch (WorkflowAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    /*
     * Run all workflows deployed in the engine without project specification
     */
    public void runAllWorkflows(String projectId) throws WorkflowNotFoundException {
        JELogger.trace(getClass(), "[projectId = " + projectId + "]"+ JEMessages.RUNNING_WFS);
        WorkflowEngineHandler.runAllWorkflows(projectId);
    }

    /*
     * Deploy a workflow to the engine
     */
    public void buildWorkflow(String projectId, String key) {
        JELogger.trace(getClass(), "[projectId = " + projectId + "] [workflow = " + key + "]" + JEMessages.DEPLOYING_WF);
        WorkflowEngineHandler.deployBPMN(projectId, key);
    }

    ///////////////////////////// Classes
    // add class
    public void addClass(ClassModel classModel) throws ClassLoadException {
        JELogger.trace(JEMessages.ADDING_CLASS + classModel.getClassName());
       JEClassCompiler.compileClass(classModel.getClassPath(), ConfigurationConstants.runnerClassLoadPath);
       try {
    	   ClassRepository.addClass(classModel.getClassId(), RuntimeDispatcher.class.getClassLoader().loadClass(ClassBuilderConfig.genrationPackageName + "." + classModel.getClassName())); ;
	} catch (ClassNotFoundException e) {
		throw new ClassLoadException("[class :"+ classModel.getClassName() +" ]"+JEMessages.CLASS_LOAD_FAILED); 
	}
        

    }

    // update class
    // delete class


    public static void injectData(JEData jeData) throws InstanceCreationFailed {
        JELogger.debug(JEMessages.INJECTING_DATA);
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
        JELogger.trace(JEMessages.ADDING_TOPICS + topics);
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

    //Trigger an event
    public void triggerEvent(String projectId, String id) throws EventException, ProjectNotFoundException {
        JELogger.trace(getClass(), "[projectId = " + projectId + "] [event = " + id + "]" + JEMessages.EVENT_TRIGGERED);
        EventManager.triggerEvent(projectId, id);
    }

    //Add an event to the runner
    public void addEvent(EventModel eventModel) {
        JEEvent e = new JEEvent(eventModel.getEventId(), eventModel.getProjectId(), eventModel.getName(), EventType.valueOf(eventModel.getEventType()), eventModel.getDescription(), eventModel.getTimeout(), eventModel.getTimeoutUnit());


        JELogger.trace(getClass(), "[projectId = " +e.getJobEngineProjectID() + "] [event = " + e.getJobEngineElementID() + "]" + JEMessages.ADDING_EVENT);

        EventManager.addEvent(eventModel.getProjectId(), e);
    }


    public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException {
        JELogger.trace("[projectId = " +projectId + "] [event = " + eventId + "]" + JEMessages.UPDATING_EVENT+" to type = " + eventType);
        EventManager.updateEventType(projectId, eventId, eventType);
    }

    public void deleteEvent(String projectId, String eventId) throws ProjectNotFoundException, EventException {
        JELogger.trace("[projectId = " +projectId + "] [event = " + eventId + "]" + JEMessages.DELETING_EVENT);
        EventManager.deleteEvent(projectId, eventId);
    }

    //clean project data from runner
    //Remove events, topics to listen to, rules and workflows
    public void removeProjectData(String projectId) {
        JELogger.trace("[projectId = " +projectId + "]"+ JEMessages.DELETING_PROJECT );
        EventManager.deleteProjectEvents(projectId);
        WorkflowEngineHandler.deleteProjectProcesses(projectId);
        RuleEngineHandler.deleteProjectRules(projectId);
        decrementTopicSubscriptionCount(projectId);
    }

    //decrement topic subscription count for a project
    public void decrementTopicSubscriptionCount(String projectId) {
        JELogger.trace("[projectId = " +projectId + "]"+ JEMessages.REMOVING_TOPIC_SUBSCRIPTION );
        for (String topic : projectsByTopic.keySet()) {
            HashSet<String> set = (HashSet<String>) projectsByTopic.get(topic);
            for (String id : set) {
                if (id.equalsIgnoreCase(projectId)) {
                    DataListener.decrementSubscriptionCount(topic);
                }
            }
        }
    }

    //remove rule topics
	public void removeRuleTopics(String projectId, String ruleId) {
		ArrayList<String> oldTopics =  (ArrayList<String>) RuleEngineHandler.getRuleTopics(projectId,ruleId);

		for(String topic : oldTopics)
		{
			DataListener.decrementSubscriptionCount(topic);
		}
		
	}

	//remove/stop workflow from runner
    public void removeWorkflow(String projectId, String workflowId) {
        JELogger.trace(getClass(), "[projectId = " + projectId + "] [workflow = " + workflowId + "]" + JEMessages.REMOVING_WF);
        WorkflowEngineHandler.deleteProcess(projectId,workflowId);
    }

    //add variable to runner
    public void addVariable(VariableModel variableModel) {
        JEVariable var = new JEVariable(variableModel.getId(),variableModel.getProjectId(),variableModel.getName(),variableModel.getType(), variableModel.getInitialValue());
        var.setJeObjectCreationDate(LocalDateTime.now());
        var.setJeObjectLastUpdate(LocalDateTime.now());
        //JEStringSubstitutor.addVariable(var.getJobEngineProjectID(), var.getName(), (String) var.getValue());
        VariableManager.addVariable(var);
    }

    //remove variable from runner
    public void deleteVariable(String projectId, String varId) {
        VariableManager.removeVariable(projectId, varId);
    }
    
	public void writeVariableValue(String projectId, String variableId, String value) {
		VariableManager.updateVariableValue(projectId,variableId, value);
		
	}

    

    public void addJarToProject(HashMap<String, String> payload) {
        JELogger.debug(ADDING_JAR_FILE_TO_RUNNER+ payload);
        //TODO finish this once the ui specs are decided
        try {
            JarFile j = new JarFile(payload.get("path"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
