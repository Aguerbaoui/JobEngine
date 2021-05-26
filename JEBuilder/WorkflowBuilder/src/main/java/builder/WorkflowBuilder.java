package builder;

import blocks.WorkflowBlock;
import blocks.basic.InformBlock;
import blocks.basic.MailBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.WebApiBlock;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEMessages;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.WorkflowConstants;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.TaskModel;
import io.je.utilities.models.WorkflowModel;
import models.JEWorkflow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.WorkflowConstants.*;

/*
 * Workflow Builder class
 * */
public class WorkflowBuilder {


    private WorkflowBuilder(){}

    /*
    * Build pbpmn and Deploy it in engine
    * */
    public static boolean buildWorkflow(JEWorkflow workflow) throws IOException, InterruptedException, ExecutionException {
        if(workflow.getWorkflowStartBlock() == null || workflow.getAllBlocks() == null || workflow.getAllBlocks().size() == 0) return false;
        if(!workflow.isScript()) {
            JEToBpmnMapper.createBpmnFromJEWorkflow(workflow);
        }
        WorkflowModel wf = new WorkflowModel();
        wf.setKey(workflow.getWorkflowName().trim());
        wf.setPath(BPMN_PATH + workflow.getWorkflowName().trim() + BPMN_EXTENSION);
        wf.setProjectId(workflow.getJobEngineProjectID());
        wf.setTriggeredByEvent(workflow.isTriggeredByEvent());
        wf.setTriggerMessage(workflow.getWorkflowStartBlock().getEventId());
        ArrayList<TaskModel> tasks = new ArrayList<>();
        for(WorkflowBlock block: workflow.getAllBlocks().values()) {
            if(block instanceof WebApiBlock) {
                TaskModel t = new TaskModel();
                t.setTaskName(block.getName());
                t.setTaskDescription(((WebApiBlock) block).getDescription());
                t.setTaskId(block.getJobEngineElementID());
                t.setType(WorkflowConstants.WEBSERVICETASK_TYPE);
                HashMap<String, Object> attributes = new HashMap<>();
                attributes.put(URL, ((WebApiBlock) block).getUrl());
                attributes.put(METHOD, ((WebApiBlock) block).getMethod());
                if(((WebApiBlock) block).getBody() != null) {
                    attributes.put(BODY, ((WebApiBlock) block).getBody());
                }
                else {
                    if (((WebApiBlock) block).getInputs() != null && ((WebApiBlock) block).getInputs().size() > 0) {
                        HashMap<String, Object> inputs = new HashMap<>();
                        for (String key : ((WebApiBlock) block).getInputs().keySet()) {
                            ArrayList<Object> input = ((WebApiBlock) block).getInputs().get(key);
                            if (input.size() == 1) {
                                inputs.put(key, input.get(0));
                            } else {
                                inputs.put(key, input);
                            }
                        }
                        attributes.put(INPUTS, inputs);
                    }
                }
                attributes.put(OUTPUTS, ((WebApiBlock) block).getOutputs());
                t.setAttributes(attributes);
                tasks.add(t);
            }
            if(block instanceof ScriptBlock) {
                TaskModel t = new TaskModel();
                t.setTaskName(block.getName());
                t.setTaskId(block.getJobEngineElementID());
                t.setType(WorkflowConstants.SCRIPTTASK_TYPE);
                HashMap<String, Object> attributes = new HashMap<>();
                attributes.put(SCRIPT, ((ScriptBlock) block).getScript());
                t.setAttributes(attributes);
                tasks.add(t);
            }
            if(block instanceof InformBlock) {
                TaskModel t = new TaskModel();
                t.setTaskName(block.getName());
                t.setTaskId(block.getJobEngineElementID());
                t.setType(WorkflowConstants.INFORMSERVICETASK_TYPE);
                HashMap<String, Object> attributes = new HashMap<>();
                attributes.put(MESSAGE, ((InformBlock) block).getMessage());
                t.setAttributes(attributes);
                tasks.add(t);
            }
            if(block instanceof MailBlock) {
                TaskModel t = new TaskModel();
                t.setTaskName(block.getName());
                t.setTaskId(block.getJobEngineElementID());
                t.setType(MAILSERVICETASK_TYPE);
                HashMap<String, Object> attributes = new HashMap<>();
                if(((MailBlock) block).isbUseDefaultCredentials()) {
                    attributes.put(WorkflowConstants.ENABLE_SSL, ((MailBlock) block).isbEnableSSL());
                    attributes.put(WorkflowConstants.USE_DEFAULT_CREDENTIALS, ((MailBlock) block).isbUseDefaultCredentials());
                }
                else {
                    attributes.put(USERNAME, ((MailBlock) block).getStrUserName());
                    attributes.put(PASSWORD, ((MailBlock) block).getStrPassword());
                }
                attributes.put(WorkflowConstants.PORT, ((MailBlock) block).getiPort());
                attributes.put(WorkflowConstants.SENDER_ADDRESS, ((MailBlock) block).getStrSenderAddress());
                attributes.put(SEND_TIME_OUT, ((MailBlock) block).getiSendTimeOut());
                attributes.put(RECEIVER_ADDRESS, ((MailBlock) block).getLstRecieverAddress());
                attributes.put(EMAIL_MESSAGE, ((MailBlock) block).getEmailMessage());
                attributes.put(SMTP_SERVER, ((MailBlock) block).getStrSMTPServer());
                t.setAttributes(attributes);
                tasks.add(t);
            }

        }
        wf.setTasks(tasks);

        workflow.setStatus(JEWorkflow.BUILDING);
        JELogger.trace(WorkflowBuilder.class, " " + JEMessages.DEPLOYING_IN_RUNNER_WORKFLOW_WITH_ID + " = " + workflow.getJobEngineElementID());
        try {
            JERunnerAPIHandler.addWorkflow(wf);
        }
        catch (JERunnerErrorException e) {
            JELogger.trace(WorkflowBuilder.class, " " + JEMessages.FAILED_TO_DEPLOY_IN_RUNNER_WORKFLOW_WITH_ID + " = " + workflow.getJobEngineElementID());
            workflow.setStatus(JEWorkflow.IDLE);
            return false;
        }
        workflow.setStatus(JEWorkflow.BUILT);
        return true;

    }


    /*
     * Run workflow in runtime engine
     * */
    public static void runWorkflow(String projectId, String key) throws IOException, InterruptedException, ExecutionException, JERunnerErrorException {
        JERunnerAPIHandler.runWorkflow(projectId, key);

    }


    public static void saveBpmn(JEWorkflow wf, String bpmn) {
        ModelBuilder.saveModel(bpmn, wf.getBpmnPath());
    }
}
