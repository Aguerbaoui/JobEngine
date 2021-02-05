package io.je.runtime.events;

import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.ResponseMessages;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.models.EventType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class EventManager {

    private static HashMap<String, HashMap<String, JEEvent>> events = new HashMap<>();

    public static void startRule(String ruleId) {
        //TODO
    }

    ///////////////////////////////////////////////////////// WORKFLOW *******************************************************************/

    /*
    * Start workflow by message
    * */
    public static void startProcessInstanceByMessage(String projectId, String messageEvent) {
        Thread thread = new Thread(() ->   {
            WorkflowEngineHandler.startProcessInstanceByMessage(projectId, messageEvent);
        });

        thread.start();
    }

    /*
    * Throw message event in workflow
    * */
    public static void throwMessageEventInWorkflow(String projectId, String event) {
        Thread thread = new Thread(() ->   {
            WorkflowEngineHandler.throwMessageEventInWorkflow(projectId, event);
        });

        thread.start();
    }

    /*
    * Throw signal event in workflow
    * */
    public static void throwSignalEventInWorkflow(String projectId, String event) {
        Thread thread = new Thread(() ->   {
            WorkflowEngineHandler.throwSignalEventInWorkflow(projectId, event);
        });
        thread.start();
    }

    /*
    * Check what type of event we have to trigger
    * */
    //TODO update with rule events
    public static void triggerEvent(String projectId, String id) {
        JEEvent event = events.get(projectId).get(id);
        if(event != null) {
            event.setTriggered(true);
            RuleEngineHandler.addEvent(event);
            if(event.getType().equals(EventType.MESSAGE_EVENT)) {
                throwMessageEventInWorkflow(projectId, event.getJobEngineElementID());
            }
            else if(event.getType().equals(EventType.SIGNAL_EVENT)) {
                throwSignalEventInWorkflow(projectId, event.getJobEngineElementID());
            }
            else if(event.getType().equals(EventType.START_WORKFLOW)) {
                startProcessInstanceByMessage(projectId, event.getJobEngineElementID());
            }
        }
    }

    /*
    * Add a new event
    * */
    public static void addEvent(String projectId, JEEvent event) {
        if(!events.containsKey(projectId)) {
            events.put(projectId, new HashMap<>());
        }
        events.get(projectId).put(event.getJobEngineElementID(), event);
        RuleEngineHandler.addEvent(event);
    }


    public static void updateEventType(String projectId, String eventId, String eventType) throws EventException, ProjectNotFoundException {
        if(!events.containsKey(projectId)) {
            throw new ProjectNotFoundException(Errors.PROJECT_NOT_FOUND);
        }
        if(!events.get(projectId).containsKey(eventId)) {
            throw new EventException(Errors.EVENT_NOT_FOUND);
        }
        EventType t = null;
        if(eventType.equalsIgnoreCase("startWorkflow")) {
            t = EventType.START_WORKFLOW;
        }
        else if(eventType.equalsIgnoreCase("signal")) {
            t = EventType.SIGNAL_EVENT;
        }
        else if(eventType.equalsIgnoreCase("message")) {
            t = EventType.MESSAGE_EVENT;
        }
        events.get(projectId).get(eventId).setType(t);
    }
}
