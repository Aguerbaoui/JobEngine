package io.je.runtime.workflow;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import io.je.JEProcess;
import io.je.processes.ProcessManager;
import io.je.serviceTasks.ActivitiTask;
import io.je.serviceTasks.DatabaseTask;
import io.je.serviceTasks.InformTask;
import io.je.serviceTasks.MailTask;
import io.je.serviceTasks.ScriptTask;
import io.je.serviceTasks.WebApiTask;
import io.je.utilities.beans.Status;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.WorkflowAlreadyRunningException;
import io.je.utilities.exceptions.WorkflowBuildException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.exceptions.WorkflowRunException;
import io.je.utilities.models.TaskModel;
import io.je.utilities.monitoring.JEMonitor;
import io.je.utilities.monitoring.MonitoringMessage;
import io.je.utilities.monitoring.ObjectType;
import utils.network.AuthScheme;
import utils.network.BodyType;
import utils.network.HttpMethod;

import static io.je.utilities.constants.WorkflowConstants.*;

/*
 * Workflow Engine handler class
 * */
public class WorkflowEngineHandler {


     private static final  HashMap<String, ProcessManager> processManagerHashMap = new HashMap<>();


    /*
     * Deploy bpmn process
     * */
    public static void deployBPMN(String projectId, String key) throws WorkflowBuildException{
        processManagerHashMap.get(projectId).deployProcess(key);
    }

    /*
     * Register workflow callbacks
     * */
    public static void registerWorkflow(String projectId, String processId) {
        processManagerHashMap.get(projectId).registerWorkflowCallback(processId, new WorkflowCallback());

    }

    /*
     * Launch process without variables
     * */
    public static void launchProcessWithoutVariables(String projectId, String processId, boolean runProject) throws WorkflowNotFoundException, WorkflowAlreadyRunningException, WorkflowBuildException, WorkflowRunException {
        processManagerHashMap.get(projectId).launchProcessByKeyWithoutVariables(processId, runProject);
    }

    /*
     * Add new process
     * */
    public static void addProcess(JEProcess process) {

        if ( !processManagerHashMap.containsKey(process.getProjectId()))
        {
            processManagerHashMap.put(process.getProjectId(), new ProcessManager());
        }
        processManagerHashMap.get(process.getProjectId()).addProcess(process);
        //registerWorkflow(process.getProjectId(), process.getKey());
        ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
    }

    public static JEProcess getProcessByID(String projectId, String id) {
        return processManagerHashMap.get(projectId).getProcessByName(id);
    }
    /*
     * Trigger event by message
     * */
    public static void throwMessageEventInWorkflow(String projectId, String msg) {
        processManagerHashMap.get(projectId).throwMessageEvent(msg);
    }

    /*
     * Trigger event by message
     * */
    public static void throwSignalEventInWorkflow(String projectId, String msg) {
        try {
            processManagerHashMap.get(projectId).throwSignal(msg);
        }
        catch (NullPointerException Ignore) {}
    }

    /*
     * Main test
     * */
    public static void main(String[] args) {
    }

    /*
    * Run all deployed workflows
    * */
    public static void runAllWorkflows(String projectId, boolean runProject) throws WorkflowNotFoundException {
       if(processManagerHashMap.containsKey(projectId))
    	{
    	   processManagerHashMap.get(projectId).runAll(projectId, runProject);
    	}
    }

    /*
    * Deploy project workflows
    * */
   /* public static void buildProject(String projectId) throws WorkflowBuildException {
        processManagerHashMap.get(projectId).buildProjectWorkflows(projectId);
    }*/

    /*
    * Stop project workflows
    * */
    public static void stopProjectWorkflows(String projectId) {
    	if(processManagerHashMap.containsKey(projectId))
    	{
            /*JELogger.debug("[projectId = " + projectId +"]"+JEMessages.STOPPING_WORKFLOW,
                    LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW,null);*/
            processManagerHashMap.get(projectId).stopProjectWorkflows();

    	}
    }

    /*
    * Start workflow by message id
    * */
    public static void startProcessInstanceByMessage(String projectId, String messageEvent) {
    	if(processManagerHashMap.containsKey(projectId))
    	{
            processManagerHashMap.get(projectId).launchProcessByMessageWithoutVariables(messageEvent);

    	}
    }

    /**/
    public static void deleteProjectProcesses(String projectId) {
      /*  JELogger.debug("[projectId = " + projectId +"]"+JEMessages.REMOVING_WFS,
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW,null);*/
        if(processManagerHashMap.containsKey(projectId)) {
            stopProjectWorkflows(projectId);
            processManagerHashMap.remove(projectId);
        }


    }

    //remove/stop workflow from runner
    public static void deleteProcess(String projectId, String workflowId) throws WorkflowRunException {
        if(processManagerHashMap.containsKey(projectId)) {
            MonitoringMessage msg = new MonitoringMessage(LocalDateTime.now(), workflowId, ObjectType.JEWORKFLOW,
                    projectId, workflowId, Status.STOPPED.toString());
            JEMonitor.publish(msg);
            processManagerHashMap.get(projectId).removeProcess(workflowId);
        }
    }

    //Parse activiti task
    public static ActivitiTask parseTask(String projectId, String workflowId, String workflowName, TaskModel task) {
        /*JELogger.debug("Parsing activiti task",
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW,workflowId);*/
        if(task.getType().equals(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            return parseWebApiTask(projectId, workflowId, workflowName, task);
        }
        else if(task.getType().equals(WorkflowConstants.SCRIPTTASK_TYPE)){
            return parseScriptTask(projectId, workflowId, workflowName, task);
        }
        else if(task.getType().equals(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
            return parseInformTask(projectId, workflowId, workflowName, task);
        }
        else if(task.getType().equals(DBREADSERVICETASK_TYPE) ||
                task.getType().equals(DBWRITESERVICETASK_TYPE) ||
                task.getType().equals(DBEDITSERVICETASK_TYPE)) {
            return parseDBTask(projectId, workflowId, workflowName, task);
        }
        else if(task.getType().equals(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            return parseMailTask(projectId, workflowId, workflowName, task);
        }
        else return null;
    }

    //parse web api task
    public static WebApiTask parseWebApiTask(String projectId, String workflowId, String workflowName,TaskModel task) {
        WebApiTask webApiTask = new WebApiTask();
        webApiTask.setBodyType(BodyType.JSON);
        webApiTask.setTaskId(task.getTaskId());
        webApiTask.setTaskName(task.getTaskName());
        webApiTask.setProcessId(workflowName);
        webApiTask.setProjectId(projectId);
        webApiTask.setWorkflowId(workflowId);
        HashMap<String, Object> attributes = task.getAttributes();
        if (attributes.get(INPUTS) != null) {
            webApiTask.setHasBody(true);
            webApiTask.setBody((HashMap<String, String>) attributes.get(INPUTS));
        } else {
            webApiTask.setHasBody(false);
            webApiTask.setStringBody((String) attributes.get(BODY));
        }
        webApiTask.setHttpMethod(HttpMethod.valueOf((String) attributes.get(METHOD)));
        webApiTask.setUrl((String) attributes.get(URL));
        if(attributes.containsKey(AUTH_SCHEME)) {
            webApiTask.setAuthentication((HashMap<String, String>) attributes.get(AUTHENTICATION));
            webApiTask.setAuthScheme(AuthScheme.valueOf((String) attributes.get(AUTH_SCHEME)));
        }
        return webApiTask;
    }

    //parse script task
    public static ScriptTask parseScriptTask(String projectId, String workflowId, String workflowName,TaskModel task) {
        ScriptTask scriptTask = new ScriptTask();
        scriptTask.setTaskName(task.getTaskName());
        scriptTask.setTaskId(task.getTaskId());
        scriptTask.setProjectId(projectId);
        scriptTask.setProcessId(workflowName);
        scriptTask.setWorkflowId(workflowId);
        HashMap<String, Object> attributes = task.getAttributes();
        if(attributes.containsKey(SCRIPT)) {
            scriptTask.setScript((String) attributes.get(SCRIPT));
            scriptTask.setTimeout((Integer) attributes.get(TIMEOUT));
        }
        return scriptTask;
    }

    //parse an inform task
    public static InformTask parseInformTask(String projectId, String workflowId, String workflowName,TaskModel task) {
        InformTask informTask = new InformTask();
        informTask.setTaskName(task.getTaskName());
        informTask.setTaskId(task.getTaskId());
        informTask.setProjectId(projectId);
        informTask.setProcessId(workflowName);
        informTask.setWorkflowId(workflowId);
        HashMap<String, Object> attributes = task.getAttributes();
        if(attributes.get(MESSAGE) != null) {
            informTask.setMessage((String) attributes.get(MESSAGE));
        }
        return informTask;
    }

    //parse database task
    public static DatabaseTask parseDBTask(String projectId, String workflowId, String workflowName,TaskModel task) {
        DatabaseTask databaseTask = new DatabaseTask();
        databaseTask.setTaskName(task.getTaskName());
        databaseTask.setTaskId(task.getTaskId());
        databaseTask.setProjectId(projectId);
        databaseTask.setProcessId(workflowName);
        databaseTask.setWorkflowId(workflowId);
        HashMap<String, Object> attributes = task.getAttributes();
        if(attributes.get(REQUEST) != null) {
            databaseTask.setRequest((String) attributes.get(REQUEST));
        }
        if(attributes.get(DATABASE_ID) != null) {
            databaseTask.setDatabaseId((String) attributes.get(DATABASE_ID));
        }
        return databaseTask;
    }

    //parse email task
    public static MailTask parseMailTask(String projectId, String workflowId, String workflowName,TaskModel task) {
        MailTask mailTask = new MailTask();
        mailTask.setTaskId(task.getTaskId());
        mailTask.setTaskName(task.getTaskName());
        mailTask.setProjectId(projectId);
        mailTask.setProcessId(workflowName);
        mailTask.setWorkflowId(workflowId);
        HashMap<String, Object> attributes = task.getAttributes();
        /*if(attributes.containsKey(B_REQUIRE_AUTHENTICATION)) {
            mailTask.setbUseDefaultCredentials((boolean) task.getAttributes().get(B_REQUIRE_AUTHENTICATION));
            mailTask.setbEnableSSL((boolean) task.getAttributes().get(ENABLE_SSL));
        }*/
        mailTask.setbEnableSSL((boolean) task.getAttributes().get(ENABLE_SSL));
        mailTask.setiPort((Integer) task.getAttributes().get(PORT));
        mailTask.setStrSenderAddress((String) task.getAttributes().get(SENDER_ADDRESS));
        mailTask.setiSendTimeOut((Integer) task.getAttributes().get(SEND_TIME_OUT));
        mailTask.setLstRecieverAddress((List<String>) task.getAttributes().get(RECEIVER_ADDRESS));
        mailTask.setEmailMessage((HashMap<String, String>) task.getAttributes().get(EMAIL_MESSAGE));
        mailTask.setStrSMTPServer((String) task.getAttributes().get(SMTP_SERVER));
        mailTask.setStrPassword((String) task.getAttributes().get(PASSWORD));
        mailTask.setStrUserName((String) task.getAttributes().get(USERNAME));
        mailTask.setLstAttachementPaths((List<String>) task.getAttributes().get(ATTACHEMENT_URLS));
        mailTask.setLstBCCs((List<String>) task.getAttributes().get(BCC_LIST));
        mailTask.setLstCCs((List<String>) task.getAttributes().get(CC_LIST));
        mailTask.setLstUploadedFiles((List<String>) task.getAttributes().get(UPLOADED_FILES_PATHS));
        return mailTask;
    }
}
