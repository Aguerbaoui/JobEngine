package io.je.runtime.events;

import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.services.RuntimeDispatcher;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.logger.JELogger;
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
    public static void triggerEvent(String projectId, String id) throws ProjectNotFoundException, EventException {
        if(!events.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEEvent event = events.get(projectId).get(id);
        if(event == null) {
            for(JEEvent ev: events.get(projectId).values()) {
                if(ev.getName().equalsIgnoreCase(id)) {
                    event = ev;
                    break;
                }
            }
        }
        if(event != null) {
            JELogger.trace(JEMessages.FOUND_EVENT + id + JEMessages.TRIGGERING_NOW);
            RuleEngineHandler.addEvent(event);
            if(event.getType().equals(EventType.MESSAGE_EVENT)) {
                throwMessageEventInWorkflow(projectId, event.getName());
            }
            else if(event.getType().equals(EventType.SIGNAL_EVENT)) {
                throwSignalEventInWorkflow(projectId, event.getName());
            }
            else if(event.getType().equals(EventType.START_WORKFLOW)) {
                startProcessInstanceByMessage(projectId, event.getName());
            }
            event.setTriggered(true);
        }
        else {
            throw new EventException(JEMessages.EVENT_NOT_FOUND);
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
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        JEEvent event = events.get(projectId).get(eventId);

        if(event == null) {
            for(JEEvent ev: events.get(projectId).values()) {
                if(ev.getName().equalsIgnoreCase(eventId)) {
                    event = ev;
                    break;
                }
            }
        }
        if(event == null) throw new EventException(JEMessages.EVENT_NOT_FOUND);
        JELogger.trace(JEMessages.FOUND_EVENT + eventId + JEMessages.UPDATING_EVENT_TYPE);
        EventType t = EventType.valueOf(eventType);
        event.setType(t);
    }

    public static void deleteEvent(String projectId, String eventId) throws ProjectNotFoundException, EventException {
        if(!events.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEEvent event = events.get(projectId).get(eventId);

        if(event == null) {
            for(JEEvent ev: events.get(projectId).values()) {
                if(ev.getName().equalsIgnoreCase(eventId)) {
                    event = events.get(projectId).remove(ev.getJobEngineElementID());
                    break;
                }
            }
            if(event == null) throw new EventException(JEMessages.EVENT_NOT_FOUND);
        }
        else {
            JELogger.trace(JEMessages.FOUND_EVENT + eventId + JEMessages.REMOVING_EVENT);
            events.get(projectId).remove(eventId);
        }
    }

    public static void deleteProjectEvents(String projectId){
        JELogger.trace(JEMessages.REMOVING_EVENTS + projectId );
        events.remove(projectId);


    }

	public static void stopEvent(String projectId, String eventId) throws ProjectNotFoundException {
		 if(!events.containsKey(projectId)) {
	            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
	        }
	        JEEvent event = events.get(projectId).get(eventId);
	        if(event == null) {
	            for(JEEvent ev: events.get(projectId).values()) {
	                if(ev.getName().equalsIgnoreCase(eventId)) {
	                    event = ev;
	                    break;
	                }
	            }
	        }
	        if(event != null) {
	        	event.setTriggered(false);
	            RuleEngineHandler.addEvent(event);
	            //TODO: add stop event in workflow

	        }
	        }
		
}

