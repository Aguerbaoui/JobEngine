package io.je.runtime.events;

import io.je.runtime.services.RuntimeDispatcher;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.beans.JEEvent;
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
            if(event.getType().equals(EventType.MESSAGE_EVENT)) {
                throwMessageEventInWorkflow(projectId, event.getReference());
            }
            else if(event.getType().equals(EventType.SIGNAL_EVENT)) {
                throwSignalEventInWorkflow(projectId, event.getReference());
            }
            else if(event.getType().equals(EventType.START_WORKFLOW)) {
                startProcessInstanceByMessage(projectId, event.getReference());
            }
            else {
                startRule(event.getReference());
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
    }


}
