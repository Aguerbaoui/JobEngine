package io.je.runtime.events;

import io.je.runtime.ruleenginehandler.RuleEngineHandler;
import io.je.runtime.workflow.WorkflowEngineHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.EventException;
import io.je.utilities.exceptions.ProjectNotFoundException;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.EventType;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.HashMap;

public class EventManager {

    private static HashMap<String, HashMap<String, JEEvent>> events = new HashMap<>();

    private static HashMap<String, HashMap<String, Thread>> activeThreads = new HashMap<>();


    public static void startRule(String ruleId) {
        //TODO
    }

    ///////////////////////////////////////////////////////// WORKFLOW *******************************************************************/

    /*
     * Start workflow by message
     * */
    public static void startProcessInstanceByMessage(String projectId, String messageEvent) {
        Thread thread = new Thread(() -> {
            WorkflowEngineHandler.startProcessInstanceByMessage(projectId, messageEvent);
        });

        thread.start();
    }

    /*
     * Start workflow by message
     * */
    public static void startProcessInstanceBySignal(String projectId, String messageEvent) {
        Thread thread = new Thread(() -> {
            WorkflowEngineHandler.throwSignalEventInWorkflow(projectId, messageEvent);
        });

        thread.start();
    }

    /*
     * Throw message event in workflow
     * */
    public static void throwMessageEventInWorkflow(String projectId, String event) {
        Thread thread = new Thread(() -> {
            WorkflowEngineHandler.throwMessageEventInWorkflow(projectId, event);
        });

        thread.start();
    }

    /*
     * Throw signal event in workflow
     * */
    public static void throwSignalEventInWorkflow(String projectId, String event) {
        Thread thread = new Thread(() -> {
            WorkflowEngineHandler.throwSignalEventInWorkflow(projectId, event);
        });
        thread.start();
    }

    /*
     * Check what type of event we have to trigger
     * */
    //TODO update with rule events
    public static void triggerEvent(String projectId, String eventId) throws ProjectNotFoundException, EventException {

        //Retrieve event
        if (!events.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEEvent event = events.get(projectId)
                .get(eventId);
        if (event == null) {
            for (JEEvent ev : events.get(projectId)
                    .values()) {
                if (ev.getJobEngineElementName()
                        .equalsIgnoreCase(eventId)) {
                    event = ev;
                    break;
                }
            }
        }
        //When event is found:
        if (event != null) {
            JELogger.trace(JEMessages.FOUND_EVENT + eventId + JEMessages.TRIGGERING_NOW,
                    LogCategory.RUNTIME, event.getJobEngineProjectID(),
                    LogSubModule.EVENT, event.getJobEngineElementID());


            JELogger.debug("[project = " + event.getJobEngineProjectName() + "] [event = " + event.getJobEngineElementName() + "]" + JEMessages.EVENT_TRIGGERED,
                    LogCategory.RUNTIME, projectId, LogSubModule.EVENT, event.getJobEngineElementID());
            //trigger event
            event.trigger();


            //Update Event in WorkflowEngine
            //TODO make this visible to the user that the event will have ONLY one type
            // It can't trigger a worklow and a block at the same time
            if (event.getType()
                    .equals(EventType.MESSAGE_EVENT)) {
                throwMessageEventInWorkflow(projectId, event.getJobEngineElementName());
            } else if (event.getType()
                    .equals(EventType.SIGNAL_EVENT)) {
                throwSignalEventInWorkflow(projectId, event.getJobEngineElementName());
            } else if (event.getType()
                    .equals(EventType.START_WORKFLOW)) {
                startProcessInstanceBySignal(projectId, event.getJobEngineElementName());
            }

            if (event.getTimeout() != 0) {
                setEventTimeOut(event);
            }

            //update event in workflow engine
            RuleEngineHandler.addEvent(event);


        } else {
            throw new EventException(JEMessages.EVENT_NOT_FOUND);
        }
    }


    private static void setEventTimeOut(JEEvent event) {
        synchronized (activeThreads) {
            Thread t = new Thread(new EventTimeoutRunnable(event));
            String projectId = event.getJobEngineProjectID();
            String eventId = event.getJobEngineElementID();
            if (activeThreads.get(projectId)
                    .get(eventId) != null) {
                activeThreads.get(projectId)
                        .get(eventId)
                        .interrupt();
            }
            activeThreads.get(projectId)
                    .put(eventId, t);
            t.start();

        }
    }

    private static void updateActiveThreads(String projectId, String eventId) {
        if (!activeThreads.containsKey(projectId)) {
            activeThreads.put(projectId, new HashMap<>());
        }
        if (activeThreads.get(projectId)
                .get(eventId) == null) {
            activeThreads.get(projectId)
                    .put(eventId, null);
        }
    }

    /*
     * Add a new event
     * */
    public static void addEvent(String projectId, JEEvent event) {
        if (!events.containsKey(projectId)) {
            events.put(projectId, new HashMap<>());
        }
        events.get(projectId)
                .put(event.getJobEngineElementID(), event);
        updateActiveThreads(projectId, event.getJobEngineElementID());
        RuleEngineHandler.addEvent(event);
    }


    public static void updateEventType(String projectId, String eventId, String eventType) throws EventException, ProjectNotFoundException {

        if (!events.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }

        JEEvent event = events.get(projectId)
                .get(eventId);

        if (event == null) {
            for (JEEvent ev : events.get(projectId)
                    .values()) {
                if (ev.getJobEngineElementName()
                        .equalsIgnoreCase(eventId)) {
                    event = ev;
                    break;
                }
            }
        }
        if (event == null) throw new EventException(JEMessages.EVENT_NOT_FOUND);
        JELogger.debug(JEMessages.FOUND_EVENT + eventId + JEMessages.UPDATING_EVENT_TYPE,
                LogCategory.RUNTIME, event.getJobEngineProjectID(),
                LogSubModule.EVENT, event.getJobEngineElementID());
        EventType t = EventType.valueOf(eventType);
        event.setType(t);
    }

    public static void deleteEvent(String projectId, String eventId) throws ProjectNotFoundException, EventException {
        if (!events.containsKey(projectId)) {
            throw new ProjectNotFoundException(JEMessages.PROJECT_NOT_FOUND);
        }
        JEEvent event = events.get(projectId)
                .get(eventId);

        if (event == null) {
            for (JEEvent ev : events.get(projectId)
                    .values()) {
                if (ev.getJobEngineElementName()
                        .equalsIgnoreCase(eventId)) {
                    event = events.get(projectId)
                            .remove(ev.getJobEngineElementID());
                    break;
                }
            }
            if (event == null) throw new EventException(JEMessages.EVENT_NOT_FOUND);
        } else {
            JELogger.debug(JEMessages.FOUND_EVENT + eventId + JEMessages.REMOVING_EVENT,
                    LogCategory.RUNTIME, event.getJobEngineProjectID(),
                    LogSubModule.EVENT, event.getJobEngineElementID());

            events.get(projectId)
                    .remove(eventId);
        }
        RuleEngineHandler.deleteEvent(projectId, eventId);

    }

    public static void deleteProjectEvents(String projectId) {
        JELogger.debug(JEMessages.REMOVING_EVENTS + projectId,
                LogCategory.RUNTIME, projectId,
                LogSubModule.EVENT, null);
        events.remove(projectId);


    }


}

