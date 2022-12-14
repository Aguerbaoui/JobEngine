package io.je.utilities.constants;

public class WorkflowConstants {


    public static final String BODY = "body";
    public static final String MESSAGE = "message";
    public static final String SCRIPT = "script";
    public static final String TIMEOUT = "timeout";
    public static final String IMPORT_DATAMODEL = "importDataModelClasses";
    public static final String ERROR_REF = "errorRef";
    public static final String EVENT_TYPE = "eventType";
    public static final String PROCESS_LISTENER_IMPLEMENTATION = "io.je.executionListeners.ProcessListener";
    public static final String ACTIVITY_LISTENER_IMPLEMENTATION = "io.je.executionListeners.ActivitiListener";
    public static final String GATEWAYS_LISTENER_IMPLEMENTATION = "io.je.executionListener" +
            "s.GatewaysListener";
    public static final String TASKS_LISTENER_IMPLEMENTATION = "io.je.executionListeners.TasksListener";
    public static final String START_PROCESS = "start";
    public static final String END_PROCESS = "end";
    public static final String DB_TASK_IMPLEMENTATION = "io.je.serviceTasks.DatabaseServiceTask";
    public static final String MAIL_TASK_IMPLEMENTATION = "io.je.serviceTasks.MailServiceTask";
    public static final String START_TYPE = "start";
    public static final String END_TYPE = "end";
    public static final String EVENTGATEWAY_TYPE = "eventgateway";
    public static final String MESSAGEINTERMEDIATECATCHEVENT_TYPE = "messageintermediatecatchevent";
    public static final String SIGNALINTERMEDIATECATCHEVENT_TYPE = "signalintermediatecatcheventtype";
    public static final String MESSAGE_THROW_EVENT_TYPE = "messagethroweventtype";
    public static final String SIGNAL_THROW_EVENT_TYPE = "signalthroweventtype";
    public static final String SEQ_FLOW_TYPE = "sequenceflow";
    public static final String EXCLUSIVEGATEWAY_TYPE = "exclusivegateway";
    public static final String SCRIPTTASK_TYPE = "scripttask";
    public static final String CALLACTIVITYTASK_TYPE = "callworkflowtask";
    public static final String PARALLELGATEWAY_TYPE = "parallelgateway";
    public static final String INCLUSIVEGATEWAY_TYPE = "inclusivegateway";
    public static final String DATETIMEREVENT = "datetimerevent";
    public static final String CYCLETIMEREVENT = "cycletimerevent";
    public static final String DURATIONTIMEREVENT = "durationtimerevent";
    public static final String DBREADSERVICETASK_TYPE = "dbreadservicetask";
    public static final String DBWRITESERVICETASK_TYPE = "dbwriteservicetask";
    public static final String DBEDITSERVICETASK_TYPE = "dbeditservicetask";
    public static final String SMS_TYPE = "sms";
    public static final String MAILSERVICETASK_TYPE = "mailservicetask";
    public static final String BUILDING = "building";
    public static final String RUNNING = "running";
    public static final String STANDBY = "standby";
    public static final String BPMN_EXTENSION = ".bpmn";
    public static final String WEBSERVICETASK_TYPE = "webtask";
    public static final String BOUNDARYEVENT_TYPE = "boundaryevent";
    public static final String INFORMSERVICETASK_TYPE = "inform";
    public static final String DESCRIPTION = "description";
    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String INPUTS = "inputs";
    public static final String OUTPUTS = "outputs";
    public static final String AUTHENTICATION = "authentication";
    public static final String AUTH_SCHEME = "authscheme";
    public static final String REQUEST = "request";
    public static final String DATABASE_ID = "databaseId";
    public static final String B_REQUIRE_AUTHENTICATION = "bRequireAuthentication";
    public static final String PORT = "iPort";
    public static final String SENDER_ADDRESS = "strSenderAddress";
    public static final String SEND_TIME_OUT = "iSendTimeOut";
    public static final String RECEIVER_ADDRESS = "lstRecieverAddress";
    public static final String CC_LIST = "lstCCs";
    public static final String BCC_LIST = "lstBCCs";
    public static final String ATTACHEMENT_URLS = "lstAttachementPaths";
    public static final String UPLOADED_FILES_PATHS = "lstUploadedFiles";
    public static final String EMAIL_MESSAGE = "emailMessage";
    public static final String SMTP_SERVER = "strSMTPServer";
    public static final String PASSWORD = "strPassword";
    public static final String USERNAME = "strUserName";
    public static final String ENABLE_SSL = "bEnableSSL";
    //region SMS
    public static final String SERVER_TYPE = "serverType";
    public static final String RECEIVER_PHONE_NUMBERS = "receiverPhoneNumbers";
    public static final String TWILIO_ACCOUNT_SID = "accountSID";
    public static final String TWILIO_ACCOUNT_TOKEN = "accountToken";
    public static final String TWILIO_SENDER_PHONE_NUMBER = "senderPhoneNumber";
    public static final String SMS_MESSAGE = "message";
    public static final String TWILIO_SERVER = "twilioServer";
    public static final String INPUT_TYPE = "inputType";
    public static final String VALIDITY = "validity";
    public static final String MODEM = "modem";
    public static final String SEND_AS_UNICODE = "sendAsUnicode";
    public static final String PRIORITY = "priority";
    public static final String SMS_EAGLE_TYPE = "smsType";
    public static final String SMS_URI = "URI";
    //endregion
    public static final String WEB_TASK_IMPLEMENTATION = "io.je.serviceTasks.WebApiServiceTask";
    public static final String SCRIPT_TASK_IMPLEMENTATION = "io.je.serviceTasks.ScriptServiceTask";
    public static final String INFORM_TASK_IMPLEMENTATION = "io.je.serviceTasks.InformServiceTask";
    public static final String SMS_TASK_IMPLEMENTATION = "io.je.serviceTasks.SMSTaskService";
    public static final String PUBLIC = "PUBLIC";
    public static final String PRIVATE = "PRIVATE";
    public static final String PROTECTED = "PROTECTED";
    public static final String ABSTRACT = "ABSTRACT";
    public static final String STATIC = "STATIC";
    public static final String VOID = "VOID";
    public static final String DATETIME = "DATETIME";
    public static final String STRING = "STRING";
    public static final String OBJECT = "OBJECT";
    public static final String BOOL = "BOOL";
    public static final String CHAR = "CHAR";
    public static final String DOUBLE = "DOUBLE";
    public static final String FLOAT = "FLOAT";
    public static final String LONG = "LONG";
    public static final String SHORT = "SHORT";
    public static final String INT = "INT";
    public static final String SBYTE = "SBYTE";
    public static final String BYTE = "BYTE";
    public static final String JEPROCEDURES = "SIOTHProcedures";
    public static final String NAME = "name";
    public static final String SUBWORKFLOWID = "subworkflowId";
    public static final String DURATION = "duration";
    public static final String ENDDATE = "enddate";
    public static final String TIMECYCLE = "timecycle";
    public static final String NBOCCURENCES = "occurrences";
    public static final String TIMEDATE = "timedate";
    public static final String EVENT_ID = "eventId";
    public static final String SOURCE_REF = "sourceRef";
    public static final String TARGET_REF = "targetRef";
    public static final String CONDITION = "condition";
    public static final String IMPORTS = "imports";
    public static final String EXECUTE_SCRIPT = "executeScript";

    private WorkflowConstants() {
    }

}
