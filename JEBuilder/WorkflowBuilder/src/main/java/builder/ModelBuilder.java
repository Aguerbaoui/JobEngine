package builder;

import io.je.utilities.beans.JEMessages;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import io.je.utilities.string.JEStringUtils;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;

import java.util.ArrayList;
import java.util.List;

public class ModelBuilder {

    public static final String BPMN = "bpmn";

    /*
     * Private Constructor
     * */
    private ModelBuilder() {
    }

    /*
     * Create new Bpmn Model
     * */
    public static BpmnModel createNewBPMNModel() {
        return new BpmnModel();
    }

    /*
     * Create an activiti process
     * */
    public static Process createProcess(String processKey) {

        Process p = new Process();
        p.setId(processKey);
        return p;
    }

    /*
     * Create a user task and return it
     * */
    public static UserTask createUserTask(String id, String name, String assignee) {
        UserTask userTask = new UserTask();
        userTask.setName(name);
        userTask.setId(id);
        userTask.setAssignee(assignee);
        return userTask;
    }

    /*
     * Create a service task and return it
     * */
    public static ServiceTask createServiceTask(String id, String name, String implementation) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setName(name);
        serviceTask.setId(id);
        serviceTask.setImplementation(implementation);
        serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        ArrayList<ActivitiListener> listeners = new ArrayList<ActivitiListener>();
        listeners.add(getListener(WorkflowConstants.TASKS_LISTENER_IMPLEMENTATION, WorkflowConstants.START_PROCESS, ImplementationType.IMPLEMENTATION_TYPE_CLASS));
        serviceTask.setExecutionListeners(listeners);
        return serviceTask;
    }

    /*
     * Create a sequence flow and return it
     * */
    public static SequenceFlow createSequenceFlow(String from, String to, String conditionExpression) {
        SequenceFlow flow = new SequenceFlow();
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        if (!JEStringUtils.isEmpty(conditionExpression)) flow.setConditionExpression(conditionExpression);
        return flow;
    }

    /*
     * Create a user start event and return it
     * */
    public static StartEvent createStartEvent(String id, String reference) {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id);
        if(reference != null) {
            MessageEventDefinition eventDefinition = new MessageEventDefinition();
            eventDefinition.setMessageRef(reference);
            startEvent.addEventDefinition(eventDefinition);
        }
        return startEvent;
    }


    /*
     * Create an end event and return it
     * */
    public static EndEvent createEndEvent(String id) {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id);
        return endEvent;
    }

    /*
     * Create a script task and return it
     * */
    public static ScriptTask createScriptTask(String id, String name, String script) {
        ScriptTask scriptTask = new ScriptTask();
        scriptTask.setName(name);
        scriptTask.setId(id);
        scriptTask.setScriptFormat("groovy");
        scriptTask.setScript(script);
        return scriptTask;
    }

    /*
     * Create an exclusive gateway and return it
     * */
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

    /*
     * Create an event based gateway and returns it
     * */
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

    /*
     * Create an inclusive gateway and returns it
     * */
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

    /*
     * Create a date timer event and returns it
     * */
    public static ThrowEvent createDateTimerEvent(String id, String name, String timeDate) {
        ThrowEvent event = new ThrowEvent();
        event.setName(name);
        event.setId(id);
        TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
        timerEventDefinition.setTimeDate(timeDate);
        event.addEventDefinition(timerEventDefinition);
        return event;
    }

    /*
     * Create a cycle timer event and returns it
     * */
    public static ThrowEvent createCycleTimerEvent(String id, String name, String timeCycle, String endDate) {
        ThrowEvent event = new ThrowEvent();
        event.setName(name);
        event.setId(id);
        TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
        timerEventDefinition.setTimeCycle(timeCycle);
        timerEventDefinition.setEndDate(endDate);
        event.addEventDefinition(timerEventDefinition);
        return event;
    }

    /*
     * Create a cycle timer event and returns it
     * */
    public static ThrowEvent createDurationTimerEvent(String id, String name, String timeDuration) {
        ThrowEvent event = new ThrowEvent();
        event.setName(name);
        event.setId(id);
        TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
        timerEventDefinition.setTimeDuration(timeDuration);
        event.addEventDefinition(timerEventDefinition);
        return event;
    }

    public static ActivitiListener getListener(String implementation, String eventType, String implementationType) {
        ActivitiListener listener = new ActivitiListener();
        listener.setImplementation(implementation);
        listener.setEvent(eventType);
        listener.setImplementationType(implementationType);
        return listener;
    }
    /*
     * Create a parallel gateway and return it
     * */
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


    /*
     * Create a message catch event and return it
     * */
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
    * Create activit error event
    * */
    public static ErrorEventDefinition createErrorEventDefinition(String errorRef) {
        ErrorEventDefinition errorEventDefinition = new ErrorEventDefinition();
        errorEventDefinition.setErrorCode("Error");
        return errorEventDefinition;
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

    /*
     * Create activit throw signal event
     * */
    public static ThrowEvent createThrowSignalEvent(String id, String name, String reference) {
        ThrowEvent event = new ThrowEvent();
        event.setName(name);
        event.setId(id);
        SignalEventDefinition eventDefinition = new SignalEventDefinition();
        eventDefinition.setSignalRef(reference);
        event.addEventDefinition(eventDefinition);
        return event;
    }

    /*
     * Save Bpmn model to a file
     * */
    public static void saveModel(BpmnModel model, String fileName) {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        try {
            JELogger.trace(JEMessages.SAVING_BPMN_FILE_TO_PATH + " = " + fileName);
            String bpmn20Xml = new String(bpmnXMLConverter.convertToXML(model), "UTF-8");
            JELogger.info(BPMN + " = \n" +  bpmn20Xml);
            JEFileUtils.copyStringToFile(bpmn20Xml, fileName, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveModel(String bpmn, String fileName) {
        try {
            JEFileUtils.copyStringToFile(bpmn, fileName, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * set Process listeners
     * */
    public static void setListenersForProcess(Process p, List<ActivitiListener> l) {
        p.setExecutionListeners(l);
    }

}
