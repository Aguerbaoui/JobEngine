package builder;

import blocks.WorkflowBlock;
import blocks.basic.InformBlock;
import blocks.basic.ScriptBlock;
import blocks.basic.WebApiBlock;
import io.je.utilities.apis.JERunnerAPIHandler;
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

import static io.je.utilities.constants.WorkflowConstants.BPMN_EXTENSION;
import static io.je.utilities.constants.WorkflowConstants.BPMN_PATH;

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
        if(workflow.getStatus().equals(JEWorkflow.BUILT)) return true;
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
                attributes.put("url", ((WebApiBlock) block).getUrl());
                attributes.put("method", ((WebApiBlock) block).getMethod());
                if(((WebApiBlock) block).getInputs() != null && ((WebApiBlock) block).getInputs().size() > 0) {
                    attributes.put("inputs", ((WebApiBlock) block).getInputs());
                    attributes.put("outputs", ((WebApiBlock) block).getOutputs());
                }
                t.setAttributes(attributes);
                tasks.add(t);
            }
            if(block instanceof ScriptBlock) {
                TaskModel t = new TaskModel();
                t.setTaskName(block.getName());
                t.setTaskId(block.getJobEngineElementID());
                t.setType(WorkflowConstants.SCRIPTTASK_TYPE);
                HashMap<String, Object> attributes = new HashMap<>();
                attributes.put("script", ((ScriptBlock) block).getScript());
                t.setAttributes(attributes);
                tasks.add(t);
            }
            if(block instanceof InformBlock) {
                TaskModel t = new TaskModel();
                t.setTaskName(block.getName());
                t.setTaskId(block.getJobEngineElementID());
                t.setType(WorkflowConstants.INFORMSERVICETASK_TYPE);
                HashMap<String, Object> attributes = new HashMap<>();
                attributes.put("message", ((InformBlock) block).getMessage());
                t.setAttributes(attributes);
                tasks.add(t);
            }

        }
        wf.setTasks(tasks);

        workflow.setStatus(JEWorkflow.BUILDING);
        JELogger.trace(WorkflowBuilder.class, " Deploying in runner workflow with id = " + workflow.getJobEngineElementID());
        try {
            JERunnerAPIHandler.addWorkflow(wf);
        }
        catch (JERunnerErrorException e) {
            JELogger.trace(WorkflowBuilder.class, " Failed to deploy in runner workflow with id = " + workflow.getJobEngineElementID());
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
        JERunnerAPIHandler.runWorkflow(JEConfiguration.getRuntimeManagerURL()+ APIConstants.RUN_WORKFLOW + projectId + "/" + key);

    }


    public static void saveBpmn(JEWorkflow wf, String bpmn) {
        ModelBuilder.saveModel(bpmn, wf.getBpmnPath());
    }
}
