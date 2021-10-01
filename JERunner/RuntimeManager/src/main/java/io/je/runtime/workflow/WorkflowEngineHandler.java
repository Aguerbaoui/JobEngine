package io.je.runtime.workflow;

import io.je.JEProcess;
import io.je.processes.ProcessManager;
import io.je.serviceTasks.*;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.WorkflowAlreadyRunningException;
import io.je.utilities.exceptions.WorkflowBuildException;
import io.je.utilities.exceptions.WorkflowNotFoundException;
import io.je.utilities.exceptions.WorkflwTriggeredByEventException;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.TaskModel;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.network.BodyType;
import utils.network.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static io.je.utilities.constants.WorkflowConstants.*;
import static io.je.utilities.constants.WorkflowConstants.URL;

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
    public static void launchProcessWithoutVariables(String projectId, String processId, boolean runProject) throws WorkflowNotFoundException, WorkflowAlreadyRunningException, WorkflwTriggeredByEventException, WorkflowBuildException {
        JELogger.debug("[projectId = " + projectId +"][workflow = "+processId+"]"+JEMessages.REMOVING_WF,
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW,processId);
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
        processManagerHashMap.get(projectId).throwSignal(msg);
    }

    /*
     * Main test
     * */
    public static void main(String[] args) {
    }

    /*
    * Run all deployed workflows
    * */
    public static void runAllWorkflows(String projectId, boolean runProject) throws WorkflowNotFoundException, WorkflowBuildException {
       if(processManagerHashMap.containsKey(projectId))
    	{
    	   processManagerHashMap.get(projectId).runAll(projectId, runProject);
    	}
    }

    /*
    * Deploy project workflows
    * */
    public static void buildProject(String projectId) throws WorkflowBuildException {
        processManagerHashMap.get(projectId).buildProjectWorkflows(projectId);
    }

    /*
    * Stop project workflows
    * */
    public static void stopProjectWorfklows(String projectId) {
    	if(processManagerHashMap.containsKey(projectId))
    	{
            JELogger.debug("[projectId = " + projectId +"]"+JEMessages.STOPPING_WORKFLOW,
                    LogCategory.RUNTIME, projectId,
                    LogSubModule.WORKFLOW,null);
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

    public static void deleteProjectProcesses(String projectId) {
        JELogger.debug("[projectId = " + projectId +"]"+JEMessages.REMOVING_WFS,
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW,null);
        if(processManagerHashMap.containsKey(projectId)) {
            stopProjectWorfklows(projectId);
            processManagerHashMap.remove(projectId);
        }


    }

    //remove/stop workflow from runner
    public static void deleteProcess(String projectId, String workflowId) {
        if(processManagerHashMap.containsKey(projectId)) {
            processManagerHashMap.get(projectId).removeProcess(workflowId);
        }
    }

    public static ActivitiTask parseTask(String projectId, String workflowId, TaskModel task) {
        JELogger.debug("Parsing activiti task",
                LogCategory.RUNTIME, projectId,
                LogSubModule.WORKFLOW,workflowId);
        if(task.getType().equals(WorkflowConstants.WEBSERVICETASK_TYPE)) {
            WebApiTask webApiTask = new WebApiTask();
            webApiTask.setBodyType(BodyType.JSON);
            webApiTask.setTaskId(task.getTaskId());
            webApiTask.setTaskName(task.getTaskName());
            webApiTask.setProcessId(workflowId);
            webApiTask.setProjectId(projectId);
            HashMap<String, Object> attributes = task.getAttributes();
            if (attributes.get(INPUTS) != null) {
                webApiTask.setHasBody(true);
                webApiTask.setBody((HashMap<String, String>) attributes.get(INPUTS));
            } else {
                webApiTask.setHasBody(true);
                webApiTask.setStringBody((String) attributes.get(BODY));
            }
            webApiTask.setHttpMethod(HttpMethod.valueOf((String) attributes.get(METHOD)));
            webApiTask.setUrl((String) attributes.get(URL));
            return webApiTask;
        }
        else if(task.getType().equals(WorkflowConstants.SCRIPTTASK_TYPE)){
            ScriptTask scriptTask = new ScriptTask();
            scriptTask.setTaskName(task.getTaskName());
            scriptTask.setTaskId(task.getTaskId());
            scriptTask.setProjectId(projectId);
            scriptTask.setProcessId(workflowId);
            HashMap<String, Object> attributes = task.getAttributes();
            if(attributes.containsKey(SCRIPT)) {
                scriptTask.setScript((String) attributes.get(SCRIPT));
                scriptTask.setTimeout((Integer) attributes.get(TIMEOUT));
            }
            return scriptTask;
        }
        else if(task.getType().equals(WorkflowConstants.INFORMSERVICETASK_TYPE)) {
            InformTask informTask = new InformTask();
            informTask.setTaskName(task.getTaskName());
            informTask.setTaskId(task.getTaskId());
            informTask.setProjectId(projectId);
            informTask.setProcessId(workflowId);
            HashMap<String, Object> attributes = task.getAttributes();
            if(attributes.get(MESSAGE) != null) {
                informTask.setMessage((String) attributes.get(MESSAGE));
            }
            return informTask;
        }
        else if(task.getType().equals(DBREADSERVICETASK_TYPE) ||
                task.getType().equals(DBWRITESERVICETASK_TYPE) ||
                task.getType().equals(DBEDITSERVICETASK_TYPE)) {
            DatabaseTask databaseTask = new DatabaseTask();
            databaseTask.setTaskName(task.getTaskName());
            databaseTask.setTaskId(task.getTaskId());
            databaseTask.setProjectId(projectId);
            databaseTask.setProcessId(workflowId);
            HashMap<String, Object> attributes = task.getAttributes();
            if(attributes.get(REQUEST) != null) {
                databaseTask.setRequest((String) attributes.get(REQUEST));
            }
            if(attributes.get(DATABASE_ID) != null) {
                databaseTask.setDatabaseId((String) attributes.get(DATABASE_ID));
            }
            return databaseTask;
        }
        else if(task.getType().equals(WorkflowConstants.MAILSERVICETASK_TYPE)) {
            MailTask mailTask = new MailTask();
            mailTask.setTaskId(task.getTaskId());
            mailTask.setTaskName(task.getTaskName());
            mailTask.setProjectId(projectId);
            mailTask.setProcessId(workflowId);

            HashMap<String, Object> attributes = task.getAttributes();
            if(attributes.containsKey(USE_DEFAULT_CREDENTIALS)) {
                mailTask.setbUseDefaultCredentials((boolean) task.getAttributes().get(USE_DEFAULT_CREDENTIALS));
                mailTask.setbEnableSSL((boolean) task.getAttributes().get(ENABLE_SSL));
            }
            mailTask.setiPort((Integer) task.getAttributes().get(PORT));
            mailTask.setStrSenderAddress((String) task.getAttributes().get(SENDER_ADDRESS));
            mailTask.setiSendTimeOut((Integer) task.getAttributes().get(SEND_TIME_OUT));
            mailTask.setLstRecieverAddress((List<String>) task.getAttributes().get(RECEIVER_ADDRESS));
            mailTask.setEmailMessage((HashMap<String, String>) task.getAttributes().get(EMAIL_MESSAGE));
            mailTask.setStrSMTPServer((String) task.getAttributes().get(SMTP_SERVER));
            mailTask.setStrPassword((String) task.getAttributes().get(PASSWORD));
            mailTask.setStrUserName((String) task.getAttributes().get(USERNAME));
            return mailTask;
        }
        else return null;
    }
}
