package builder;

import blocks.WorkflowBlock;
import blocks.basic.*;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.Status;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.WorkflowRunException;
import io.je.utilities.log.JELogger;
import io.je.utilities.models.TaskModel;
import io.je.utilities.models.WorkflowModel;
import models.JEWorkflow;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.ArrayList;
import java.util.HashMap;

import static io.je.utilities.constants.WorkflowConstants.*;

/*
 * Workflow Builder class
 * */
public class WorkflowBuilder {


    private WorkflowBuilder() {
    }


    /*Get task model for workflows*/
    private static TaskModel getTaskModel(String taskId, String taskName, String taskDescription, String taskType) {
        TaskModel t = new TaskModel();
        t.setTaskName(taskName);
        t.setTaskDescription(taskDescription);
        t.setTaskId(taskId);
        t.setType(taskType);
        return  t;
    }

    /*Get attributes map for web api task*/
    private static HashMap<String, Object > getWebApiAttributesMap(WebApiBlock WebApiBlock) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(URL, WebApiBlock.getUrl());
        attributes.put(METHOD, WebApiBlock.getMethod());
        if (WebApiBlock.getBody() != null) {
            attributes.put(BODY, WebApiBlock.getBody());
        } else {
            if (WebApiBlock.getInputs() != null && WebApiBlock.getInputs().size() > 0) {
                HashMap<String, Object> inputs = new HashMap<>();
                for (String key : WebApiBlock.getInputs().keySet()) {
                    ArrayList<Object> input = WebApiBlock.getInputs().get(key);
                    if (input.size() == 1) {
                        inputs.put(key, input.get(0));
                    } else {
                        inputs.put(key, input);
                    }
                }
                attributes.put(INPUTS, inputs);
            }
        }
        attributes.put(OUTPUTS, WebApiBlock.getOutputs());
        return attributes;
    }

    /*Get attributes map for script task*/
    private static HashMap<String, Object > getScriptAttributesMap(ScriptBlock block) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(SCRIPT,  block.getScript());
        attributes.put(TIMEOUT,  block.getTimeout());
        return attributes;
    }

    /*Get attributes map for inform task*/
    private static HashMap<String, Object > getInformAttributesMap(InformBlock block) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(MESSAGE, block.getMessage());
        return attributes;
    }

    /*Get attributes map for inform task*/
    private static HashMap<String, Object > getDBReadTaskAttributesMap(DBReadBlock block) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(REQUEST, block.getRequest());
        attributes.put(DATABASE_ID, block.getDatabaseId());
        return attributes;
    }

    /*Get attributes map for inform task*/
    private static HashMap<String, Object > getDBWriteTaskAttributesMap(DBWriteBlock block) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(REQUEST, block.getRequest());
        attributes.put(DATABASE_ID, block.getDatabaseId());
        return attributes;
    }

    /*Get attributes map for inform task*/
    private static HashMap<String, Object > getDBEditTaskAttributesMap(DBEditBlock block) {
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put(REQUEST, block.getRequest());
        attributes.put(DATABASE_ID, block.getDatabaseId());
        return attributes;
    }
    /*Get attributes map for email task*/
    private static HashMap<String, Object> getEmailTaskAttributesMap(MailBlock block) {
        HashMap<String, Object> attributes = new HashMap<>();
        if (block.isbUseDefaultCredentials()) {
            attributes.put(WorkflowConstants.ENABLE_SSL, block.isbEnableSSL());
            attributes.put(WorkflowConstants.USE_DEFAULT_CREDENTIALS, block.isbUseDefaultCredentials());
        } else {
            attributes.put(USERNAME, block.getStrUserName());
            attributes.put(PASSWORD, block.getStrPassword());
        }
        attributes.put(WorkflowConstants.PORT, block.getiPort());
        attributes.put(WorkflowConstants.SENDER_ADDRESS, block.getStrSenderAddress());
        attributes.put(SEND_TIME_OUT, block.getiSendTimeOut());
        attributes.put(RECEIVER_ADDRESS, block.getLstRecieverAddress());
        attributes.put(EMAIL_MESSAGE, block.getEmailMessage());
        attributes.put(SMTP_SERVER, block.getStrSMTPServer());
        return attributes;
    }


    /*
     * Build pbpmn and Deploy it in engine
     * */
    public static boolean buildWorkflow(JEWorkflow workflow) {
        if (workflow.getWorkflowEndBlock() == null ||
                workflow.getWorkflowStartBlock() == null ||
                workflow.getAllBlocks() == null ||
                workflow.getAllBlocks().size() == 0 ||
                workflow.isHasErrors())
            return false;
        if (!workflow.isScript()) {
            JEToBpmnMapper.createBpmnFromJEWorkflow(workflow);
        }
        WorkflowModel wf = new WorkflowModel();
        wf.setId(workflow.getJobEngineElementName().trim());
        wf.setPath(ConfigurationConstants.BPMN_PATH + workflow.getJobEngineElementName().trim() + BPMN_EXTENSION);
        wf.setProjectId(workflow.getJobEngineProjectID());
        wf.setTriggeredByEvent(workflow.isTriggeredByEvent());
        wf.setTriggerMessage(workflow.getWorkflowStartBlock().getEventId());
        wf.setOnProjectBoot(workflow.isOnProjectBoot());
        wf.setProjectName(workflow.getJobEngineProjectName());
        ArrayList<TaskModel> tasks = new ArrayList<>();
        for (WorkflowBlock block : workflow.getAllBlocks().values()) {
            if (block instanceof WebApiBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.WEBSERVICETASK_TYPE);
                t.setAttributes(getWebApiAttributesMap((WebApiBlock) block));
                tasks.add(t);
            }
            if (block instanceof ScriptBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.SCRIPTTASK_TYPE);
                t.setAttributes(getScriptAttributesMap((ScriptBlock) block));
                tasks.add(t);
            }
            if (block instanceof InformBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.INFORMSERVICETASK_TYPE);
                t.setAttributes(getInformAttributesMap((InformBlock) block));
                tasks.add(t);
            }
            if(block instanceof DBReadBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.DBREADSERVICETASK_TYPE);
                t.setAttributes(getDBReadTaskAttributesMap((DBReadBlock) block));
                tasks.add(t);
            }
            if(block instanceof DBWriteBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.DBWRITESERVICETASK_TYPE);
                t.setAttributes(getDBWriteTaskAttributesMap((DBWriteBlock) block));
                tasks.add(t);
            }
            if(block instanceof DBEditBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.DBEDITSERVICETASK_TYPE);
                t.setAttributes(getDBEditTaskAttributesMap((DBEditBlock) block));
                tasks.add(t);
            }
            if (block instanceof MailBlock) {
                TaskModel t = getTaskModel(block.getJobEngineElementID(), block.getJobEngineElementName(), block.getDescription(), WorkflowConstants.MAILSERVICETASK_TYPE);
                t.setAttributes(getEmailTaskAttributesMap((MailBlock) block));
                tasks.add(t);
            }

        }
        wf.setTasks(tasks);
        workflow.setStatus(Status.BUILDING);
        String endEventId = workflow.getWorkflowEndBlock().getEventId();
        if(endEventId != null) {
            wf.setEndBlockEventId(endEventId);
        }
        JELogger.debug( JEMessages.DEPLOYING_IN_RUNNER_WORKFLOW_WITH_ID + " = " + workflow.getJobEngineElementID(),
                LogCategory.DESIGN_MODE, workflow.getJobEngineProjectID(),
                LogSubModule.WORKFLOW, workflow.getJobEngineElementID());
        try {
            JERunnerAPIHandler.addWorkflow(wf);
        } catch (JERunnerErrorException e) {
            JELogger.error( JEMessages.FAILED_TO_DEPLOY_IN_RUNNER_WORKFLOW_WITH_ID + " = " + workflow.getJobEngineElementID(),
                    LogCategory.DESIGN_MODE, workflow.getJobEngineProjectID(),
                    LogSubModule.WORKFLOW, workflow.getJobEngineElementID());
            workflow.setStatus(Status.NOT_BUILT);
            return false;
        }

        workflow.setStatus(Status.STOPPED);
        return true;

    }


    /*
     * Run workflow in runtime engine
     * */
    public static void runWorkflow(String projectId, String key) throws WorkflowRunException {
        try {
            JERunnerAPIHandler.runWorkflow(projectId, key);
        }
        catch(JERunnerErrorException e) {
            throw new WorkflowRunException(JEMessages.WORKFLOW_RUN_ERROR + e.getMessage());
        }


    }

    /*
    * Saving bpmn into a temp file
    * */
    public static void saveBpmn(JEWorkflow wf, String bpmn) {
        ModelBuilder.saveModel(bpmn, wf.getBpmnPath());
    }
}
