package io.je.runtime.services;

import io.je.runtime.data.DataListener;
import io.je.runtime.events.EventManager;
import io.je.runtime.models.ClassModel;
import io.je.runtime.models.RuleModel;
import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.beans.JEData;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.classloader.ClassManager;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventModel;
import io.je.utilities.models.EventType;
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
        DataListener.startListening(topics);
        RuleEngineHandler.runRuleEngineProject(projectId);
        WorkflowEngineHandler.runAllWorkflows(projectId);
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
    }

    // update rule
    public void updateRule(RuleModel ruleModel)
            throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

        RuleEngineHandler.updateRule(ruleModel);

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
    public void launchProcessWithoutVariables(String projectId, String key) throws WorkflowNotFoundException, WorkflwTriggeredByEventException, WorkflowAlreadyRunningException {
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

    ///////////////////////////// Classes
    // add class
    public void addClass(ClassModel classModel) throws ClassLoadException {

        ClassManager.loadClass(classModel.getClassId(), classModel.getClassName(), classModel.getClassPath());

    }

    // update class
    // delete class


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
        JELogger.trace(" Adding event ");
        EventManager.addEvent(eventModel.getProjectId(), e);
    }


    public void updateEventType(String projectId, String eventId, String eventType) throws ProjectNotFoundException, EventException {
        JELogger.trace(" updating event id = " + eventId + " in project id = " + projectId);
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
}
