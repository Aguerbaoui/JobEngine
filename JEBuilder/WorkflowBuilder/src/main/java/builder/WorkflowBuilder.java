package builder;

import blocks.WorkflowBlock;
import blocks.basic.*;
import blocks.control.EventGatewayBlock;
import blocks.control.ExclusiveGatewayBlock;
import blocks.control.ParallelGatewayBlock;
import blocks.events.MessageCatchEvent;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;
import models.JEWorkflow;

import java.io.IOException;
import java.util.HashMap;

/*
 * Workflow Builder class
 * */
public class WorkflowBuilder {


    /*
    * Build workflow bpmn
    * */
    public static void buildWorkflow(JEWorkflow workflow) {
        //JEToBpmnMapper.createBpmnFromJEWorkflow(workflow);
        /*
         * testing purposes only
         * */
        JEToBpmnMapper.launchBuildTest(workflow);

        HashMap<String, String> wfMap = new HashMap<String, String>();
        wfMap.put("key", workflow.getWorkflowName().trim());
        wfMap.put("path", "processes/" + workflow.getWorkflowName().trim() + ".bpmn");
        wfMap.put("projectId", workflow.getJobEngineProjectID());
        try {
            Network.makeNetworkCallWithJsonBody(wfMap, APIConstants.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_WORKFLOW);
        } catch (IOException e) {
            JELogger.info(JEToBpmnMapper.class, "Network Error");
        }
    }


    /*
     * Run workflow in runtime engine
     * */
    public static void runWorkflow(String key) throws IOException {
        Network.makeNetworkCall(APIConstants.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_WORKFLOW + key);

    }


}
