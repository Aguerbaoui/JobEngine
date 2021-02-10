package builder;

import blocks.WorkflowBlock;
import blocks.events.ThrowMessageEvent;
import blocks.events.ThrowSignalEvent;
import com.squareup.okhttp.Response;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.EventModel;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;
import models.JEWorkflow;
import org.springframework.ui.Model;

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


    /*
    * Build workflow bpmn
    * */
    public static boolean buildWorkflow(JEWorkflow workflow) throws IOException, JERunnerErrorException, InterruptedException, ExecutionException {
        //JEToBpmnMapper.createBpmnFromJEWorkflow(workflow);
        //TODO fix this shit will u still just testing atm
        /*
         * testing purposes only
         * */
        if(!workflow.isScript()) {
            JEToBpmnMapper.createBpmnFromJEWorkflow(workflow);
        }
     /*   HashMap<String, Object> wfMap = new HashMap<String, Object>();
        wfMap.put("key", workflow.getWorkflowName().trim());
        wfMap.put("path", "processes/" + workflow.getWorkflowName().trim() + ".bpmn");
        wfMap.put("projectId", workflow.getJobEngineProjectID());*/
        WorkflowModel wf = new WorkflowModel();
        wf.setKey(workflow.getWorkflowName().trim());
        wf.setPath(BPMN_PATH + workflow.getWorkflowName().trim() + BPMN_EXTENSION);
        wf.setProjectId(workflow.getJobEngineProjectID());
      /*  ArrayList<EventModel> events = new ArrayList<>();
        for(WorkflowBlock block: workflow.getAllBlocks().values()) {
            if(block instanceof ThrowMessageEvent) {
                events.add(new EventModel(block.getJobEngineElementID(), block.getJobEngineProjectID(),  JEEvent.START_WORKFLOW, ((ThrowMessageEvent) block).getMessageRef(), block.getName()));
            }
            else if(block instanceof ThrowSignalEvent) {
                events.add(new EventModel(block.getJobEngineElementID(), block.getJobEngineProjectID(),  JEEvent.START_WORKFLOW, ((ThrowSignalEvent) block).getMessageRef(), block.getName()));
            }
        }
        wf.setEvents(events);*/
        wf.setTriggeredByEvent(workflow.isTriggeredByEvent());
        workflow.setStatus(JEWorkflow.BUILDING);
        JELogger.trace(WorkflowBuilder.class, "Deploying in runner workflow with id = " + workflow.getJobEngineElementID());
        try {
            JERunnerAPIHandler.addWorkflow(wf);
        }
        catch (JERunnerErrorException e) {
            JELogger.trace(WorkflowBuilder.class, "Dailed to deploy in runner workflow with id = " + workflow.getJobEngineElementID());
            return false;
        }
        //Network.makeNetworkCallWithJsonObjectBodyWithResponse(wf, JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_WORKFLOW);
        workflow.setStatus(JEWorkflow.BUILT);
        return true;

    }


    /*
     * Run workflow in runtime engine
     * */
    public static void runWorkflow(String projectId, String key) throws IOException, InterruptedException, ExecutionException {
        //Response response = Network.makeGetNetworkCallWithResponse(JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.RUN_WORKFLOW + projectId + "/" + key).get();

    }


    public static void saveBpmn(JEWorkflow wf, String bpmn) {
        ModelBuilder.saveModel(bpmn, wf.getBpmnPath());
    }
}
