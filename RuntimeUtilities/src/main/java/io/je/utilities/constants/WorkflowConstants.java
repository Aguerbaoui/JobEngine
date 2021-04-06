package io.je.utilities.constants;

public class WorkflowConstants {


    private WorkflowConstants() {}
    public static final  String PROCESS_LISTENER_IMPLEMENTATION = "io.je.executionListeners.ProcessListener";
    public static final  String GATEWAYS_LISTENER_IMPLEMENTATION = "io.je.executionListeners.GatewaysListener";
    public static final  String TASKS_LISTENER_IMPLEMENTATION = "io.je.executionListeners.TasksListener";
    public static final  String START_PROCESS = "start";
    public static final  String END_PROCESS = "end";
    public static final  String DB_WRITE_TASK_IMPLEMENTATION = "io.je.serviceTasks.DatabaseInsertServiceTask";
    public static final  String MAIL_TASK_IMPLEMENTATION = "io.je.serviceTasks.MailServiceTask";
    public static final  String START_TYPE = "start";
    public static final  String END_TYPE = "end";
    public static final  String EVENTGATEWAY_TYPE = "eventgateway";
    public static final  String MESSAGEINTERMEDIATECATCHEVENT_TYPE = "messageintermediatecatchevent";
    public static final  String SIGNALINTERMEDIATECATCHEVENT_TYPE = "signalintermediatecatcheventType";
    public static final  String MESSAGE_THROW_EVENT_TYPE = "messageThrowEventType";
    public static final  String SIGNAL_THROW_EVENT_TYPE = "signalThrowEventType";
    public static final String SEQ_FLOW_TYPE = "sequenceflow";
    public static final String EXCLUSIVEGATEWAY_TYPE = "exclusivegateway";
    public static final String SCRIPTTASK_TYPE = "scripttask";
    public static final String PARALLELGATEWAY_TYPE = "parallelgateway";
    public static final String INCLUSIVEGATEWAY_TYPE = "inclusivegateway";
    public static final String DATETIMEREVENT = "datetimerevent";
    public static final String CYCLETIMEREVENT = "cycletimerevent";
    public static final String DURATIONTIMEREVENT = "durationtimerevent";
    public static final String DBSERVICETASK_TYPE = "dbservicetask";
    public static final String MAILSERVICETASK_TYPE = "email";
    public static final String BUILDING = "building";
    public static final String RUNNING = "running";
    public static final String STANDBY = "standby";
    public static final String BPMN_PATH = "D:\\processes\\";
    public static final String BPMN_EXTENSION = ".bpmn";
    public static final String WEBSERVICETASK_TYPE = "webtask";
    public static final String INFORMSERVICETASK_TYPE = "inform";
    public static final String DESCRIPTION = "description";
    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String INPUTS = "inputs";
    public static final String OUTPUTS = "outputs";
    public static final String USE_DEFAULT_CREDENTIALS = "bUseDefaultCredentials";
    public static final String PORT = "iPort";
    public static final String SENDER_ADDRESS = "strSenderAddress";
    public static final String SEND_TIME_OUT = "iSendTimeOut";
    public static final String RECEIVER_ADDRESS = "lstRecieverAddress";
    public static final String EMAIL_MESSAGE = "emailMessage";
    public static final String SMTP_SERVER = "strSMTPServer";
    public static final String PASSWORD = "strPassword";
    public static final String USERNAME = "strUserName";
    public static final String ENABLE_SSL = "bEnableSSL";
    public static final  String WEB_TASK_IMPLEMENTATION = "io.je.serviceTasks.WebApiServiceTask";
    public static final String SCRIPT_TASK_IMPLEMENTATION = "io.je.serviceTasks.ScriptServiceTask";
    public static final String INFORM_TASK_IMPLEMENTATION = "io.je.serviceTasks.InformServiceTask";

}
