package io.je.utilities.constants;

public class WorkflowConstants {

    public final static String processListenerImplementation = "io.je.executionListeners.ProcessListener";
    public final static String startProcess = "start";
    public final static String endProcess = "end";
    public final static String dbWriteTaskImplementation = "io.je.serviceTasks.DatabaseInsertServiceTask";
    public final static String mailTaskImplementation = "io.je.serviceTasks.MailServiceTask";
    public final static String startType = "start";
    public final static String endType = "end";
    public final static String eventgatewayType = "eventgateway";
    public final static String messageintermediatecatcheventType = "messageintermediatecatchevent";
    public final static String signalintermediatecatcheventType = "signalintermediatecatcheventType";
    public final static String messageThrowEventType = "messageThrowEventType";
    public final static String signalThrowEventType = "signalThrowEventType";
    public final static String seqFlowType = "sequenceflow";
    public final static String exclusivegatewayType = "exclusivegateway";
    public final static String scripttaskType = "scripttask";
    public final static String parallelgatewayType = "parallelgateway";
    public final static String inclusivegatewayType = "inclusivegateway";
    public final static String datetimerevent = "datetimerevent";
    public final static String cycletimerevent = "cycletimerevent";
    public final static String durationtimerevent = "durationtimerevent";
    public final static String dbservicetaskType = "dbservicetask";
    public final static String mailservicetaskType = "mailservicetask";
    public final static String BUILDING = "building";
    public final static String RUNNING = "running";
    public final static String STANDBY = "standby";
    public final static String bpmnPath = "D:\\Job engine\\JERunner\\WorkflowEngine\\src\\main\\resources\\processes\\";
    public final static String bpmnExtension = ".bpmn";

}
