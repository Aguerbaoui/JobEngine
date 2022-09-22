package builder;

import blocks.events.TimerEvent;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.Timers;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.log.JELogger;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import utils.files.FileUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.string.StringUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ModelBuilder {

    public static final String BPMN = "bpmn";

    /**
     * Private Constructor
     */
    private ModelBuilder() {
    }

    /**
     * Create new Bpmn Model
     */
    public static BpmnModel createNewBPMNModel() {
        return new BpmnModel();
    }

    /**
     * Create an activiti process
     */
    public static Process createProcess(String processKey) {

        Process p = new Process();
        p.setId(processKey);
        return p;
    }

    /**
     * Create a user task and return it
     */
    public static UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }

    /**
     * Create a service task and return it
     */
    public static ServiceTask createServiceTask(String id, String name, String implementation) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setName(name);
        serviceTask.setId(id);
        serviceTask.setImplementation(implementation);
        //serviceTask.setAsynchronous(true);
        serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        ArrayList<ActivitiListener> listeners = new ArrayList<>();
        listeners.add(getListener(WorkflowConstants.TASKS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        serviceTask.setExecutionListeners(listeners);
        return serviceTask;
    }

    public static ActivitiListener getListener(String implementation, String eventType, String implementationType) {
        ActivitiListener listener = new ActivitiListener();
        listener.setImplementation(implementation);
        listener.setEvent(eventType);
        listener.setImplementationType(implementationType);
        return listener;
    }

    /**
     * Create a sequence flow and return it
     */
    public static SequenceFlow createSequenceFlow(String from, String to, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        if (!StringUtilities.isEmpty(conditionExpression)) flow.setConditionExpression(conditionExpression);
        return flow;
    }

    /**
     * Create a user start event and return it
     */
    public static StartEvent createStartEvent(String id, String reference, TimerEvent timerEvent) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        if (reference != null) {
            SignalEventDefinition eventDefinition = new SignalEventDefinition();
            eventDefinition.setSignalRef(reference);
            startEvent.addEventDefinition(eventDefinition);
        } else if (timerEvent != null) {
            if (timerEvent.getTimer()
                    .equals(Timers.DELAY)) {
                startEvent.addEventDefinition(createTimerEvent(timerEvent.getTimeDuration(), null, null, null));
            } else if (timerEvent.getTimer()
                    .equals(Timers.CYCLIC)) {
                startEvent.addEventDefinition(createTimerEvent(null, null, timerEvent.getTimeCycle(), timerEvent.getEndDate()));
            } else {
                startEvent.addEventDefinition(createTimerEvent(null, timerEvent.getTimeDate(), null, null));
            }

        }

        return startEvent;
    }

    public static TimerEventDefinition createTimerEvent(String timerDelay, String timerDate, String timerCycle, String endDate) {
        TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
        if (timerDelay != null) {
            timerEventDefinition.setTimeDuration(timerDelay);
        }

        if (timerDate != null) {
            timerEventDefinition.setTimeDate(timerDate);
        }

        if (timerCycle != null) {
            timerEventDefinition.setTimeCycle(timerCycle);
            timerEventDefinition.setTimeDate(null);
        }
        if (endDate != null) {
            timerEventDefinition.setEndDate(endDate);
        }

        return timerEventDefinition;
    }

    /**
     * Create an end event and return it
     */
    public static EndEvent createEndEvent(String id) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        return endEvent;
    }

    /**
     * Create a script task and return it
     */
    public static ScriptTask createScriptTask(String id, String name, String script) {
        ScriptTask scriptTask = new ScriptTask();
        scriptTask.setName(name);
        scriptTask.setId(id);
        scriptTask.setScriptFormat("groovy");
        scriptTask.setScript(script);
        return scriptTask;
    }

    /**
     * Create an exclusive gateway and return it
     */
    public static ExclusiveGateway createExclusiveGateway(String id, String name, boolean exclusive, List<SequenceFlow> inFlows, List<SequenceFlow> outFlows) {
        ExclusiveGateway gateway = new ExclusiveGateway();
        gateway.setName(name);
        gateway.setId(id);
        gateway.setExclusive(exclusive);
        gateway.setIncomingFlows(inFlows);
        gateway.setOutgoingFlows(outFlows);
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(getListener(WorkflowConstants.GATEWAYS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        gateway.setExecutionListeners(listeners);
        return gateway;
    }

    /**
     * Create an event based gateway and returns it
     */
    public static EventGateway createEventGateway(String id, String name, boolean exclusive, List<SequenceFlow> inFlows, List<SequenceFlow> outFlows) {
        EventGateway gateway = new EventGateway();
        gateway.setName(name);
        gateway.setId(id);
        gateway.setExclusive(exclusive);
        gateway.setIncomingFlows(inFlows);
        gateway.setOutgoingFlows(outFlows);
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(getListener(WorkflowConstants.GATEWAYS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        gateway.setExecutionListeners(listeners);
        return gateway;
    }

    /**
     * Create an inclusive gateway and returns it
     */
    public static InclusiveGateway createInclusiveGateway(String id, String name, boolean exclusive, List<SequenceFlow> inFlows, List<SequenceFlow> outFlows) {
        InclusiveGateway gateway = new InclusiveGateway();
        gateway.setName(name);
        gateway.setId(id);
        gateway.setExclusive(exclusive);
        gateway.setIncomingFlows(inFlows);
        gateway.setOutgoingFlows(outFlows);
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(getListener(WorkflowConstants.GATEWAYS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        gateway.setExecutionListeners(listeners);
        return gateway;
    }

    /**
     * Create a date timer event and returns it
     */
    public static IntermediateCatchEvent createDateTimerEvent(String id, String name, String timeDate) {
        IntermediateCatchEvent event = new IntermediateCatchEvent();
        event.setName(name);
        event.setId(id);
        event.addEventDefinition(createTimerEvent(null, timeDate, null, null));
        return event;
    }

    /**
     * Create a cycle timer event and returns it
     */
    public static BoundaryEvent createCycleTimerEvent(String id, String name, String timeCycle, String endDate, String attachedRef, Activity attachedTo) {
        BoundaryEvent event = new BoundaryEvent();
        event.setName(name);
        event.setId(id);
        event.setCancelActivity(true);
        event.setAttachedToRefId(attachedRef);
        event.setAttachedToRef(attachedTo);
        event.addEventDefinition(createTimerEvent(null, null, timeCycle, endDate));
        return event;
    }

    /**
     * Create a cycle timer event and returns it
     */
    public static IntermediateCatchEvent createDurationTimerEvent(String id, String name, String timeDuration) {
        IntermediateCatchEvent event = new IntermediateCatchEvent();
        event.setName(name);
        event.setId(id);
        event.addEventDefinition(createTimerEvent(timeDuration, null, null, null));
        return event;
    }

    /**
     * Create a parallel gateway and return it
     */
    public static ParallelGateway createParallelGateway(String id, String name, List<SequenceFlow> inFlows, List<SequenceFlow> outFlows) {
        ParallelGateway gateway = new ParallelGateway();
        gateway.setName(name);
        gateway.setId(id);
        gateway.setIncomingFlows(inFlows);
        gateway.setOutgoingFlows(outFlows);
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(getListener(WorkflowConstants.GATEWAYS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        gateway.setExecutionListeners(listeners);
        return gateway;
    }

    /**
     * Create a message catch event and return it
     */
    public static IntermediateCatchEvent createMessageIntermediateCatchEvent(String id, String name, String messageRef) {
        IntermediateCatchEvent event = new IntermediateCatchEvent();
        event.setName(name);
        event.setId(id);
        MessageEventDefinition eventDefinition = new MessageEventDefinition();
        eventDefinition.setMessageRef(messageRef);
        event.addEventDefinition(eventDefinition);
        return event;
    }

    public static IntermediateCatchEvent createSignalIntermediateCatchEvent(String id, String name, String messageRef) {
        IntermediateCatchEvent event = new IntermediateCatchEvent();
        event.setName(name);
        event.setId(id);
        SignalEventDefinition eventDefinition = new SignalEventDefinition();
        eventDefinition.setSignalRef(messageRef);
        event.addEventDefinition(eventDefinition);
        return event;
    }

    public static ThrowEvent createThrowMessageEvent(String id, String name, String reference) {
        ThrowEvent event = new ThrowEvent();
        event.setName(name);
        event.setId(id);
        MessageEventDefinition eventDefinition = new MessageEventDefinition();
        eventDefinition.setMessageRef(reference);
        event.addEventDefinition(eventDefinition);
        return event;
    }

    /*
     * Create activit boundary event
     * */
    public static BoundaryEvent createBoundaryEvent(String id, String attachedToId, Activity attachedToElement, String errorRef) {
        BoundaryEvent b = new BoundaryEvent();
        b.setId(id);
        b.setAttachedToRefId(attachedToId);
        b.setAttachedToRef(attachedToElement);
        b.setCancelActivity(true);
        b.addEventDefinition(createErrorEventDefinition(errorRef));
        return b;
    }

    /**
     * Create activiti error event
     */
    public static ErrorEventDefinition createErrorEventDefinition(String errorRef) {
        ErrorEventDefinition errorEventDefinition = new ErrorEventDefinition();
        errorEventDefinition.setErrorCode("Error");
        return errorEventDefinition;
    }

    /**
     * Create activiti throw signal event
     */
    public static ThrowEvent createThrowSignalEvent(String id, String name, String reference) {
        ThrowEvent event = new ThrowEvent();
        event.setName(name);
        event.setId(id);
        SignalEventDefinition eventDefinition = new SignalEventDefinition();
        eventDefinition.setSignalRef(reference);
        event.addEventDefinition(eventDefinition);
        return event;
    }

    public static CallActivity createCallActivity(String id, String name, String calledElement) {
        CallActivity callActivity = new CallActivity();
        callActivity.setCalledElement(calledElement);
        callActivity.setId(id);
        callActivity.setName(name);
        return callActivity;
    }

    /**
     * Save Bpmn model to a file
     */
    public static void saveModel(BpmnModel model, String fileName) {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        try {
            JELogger.debug(JEMessages.SAVING_BPMN_FILE_TO_PATH + " = " + fileName,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.WORKFLOW, null);
            String bpmn20Xml = new String(bpmnXMLConverter.convertToXML(model), "UTF-8");
            JELogger.debug(BPMN + " = \n" + bpmn20Xml,
                    LogCategory.DESIGN_MODE, null,
                    LogSubModule.WORKFLOW, null);
            FileUtilities.copyStringToFile(bpmn20Xml, fileName, "utf-8");
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }

    public static void saveModel(String bpmn, String fileName) {
        try {
            FileUtilities.copyStringToFile(bpmn, fileName, "utf-8");
        } catch (Exception e) {
            LoggerUtils.logException(e);
        }
    }

    /**
     * set Process listeners
     */
    public static void setListenersForProcess(Process p, List<ActivitiListener> l) {
        p.setExecutionListeners(l);
    }

}
