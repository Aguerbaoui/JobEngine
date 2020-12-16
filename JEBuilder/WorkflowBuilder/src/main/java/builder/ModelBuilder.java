package builder;

import io.je.utilities.files.JEFileUtils;
import io.je.utilities.logger.JELogger;
import io.je.utilities.string.JEStringUtils;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;

import java.util.List;

public class ModelBuilder {

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
    public static StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start1");
        return startEvent;
    }

    /*
     * Create an end event and return it
     * */
    public static EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId("end1");
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
        return gateway;
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

    /*
     * Save Bpmn model to a file
     * */
    public static void saveModel(BpmnModel model, String fileName) {
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
        try {
            JELogger.info(ModelBuilder.class, fileName);
            JELogger.info(ModelBuilder.class, model.toString());
            String bpmn20Xml = new String(bpmnXMLConverter.convertToXML(model), "UTF-8");
            JELogger.info(ModelBuilder.class, bpmn20Xml);
            JEFileUtils.copyStringToFile(bpmn20Xml, fileName, "utf-8");
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
