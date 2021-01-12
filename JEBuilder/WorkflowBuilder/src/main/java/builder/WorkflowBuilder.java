package builder;

import com.squareup.okhttp.Response;
import io.je.utilities.constants.APIConstants;
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
    public static void buildWorkflow(JEWorkflow workflow) throws IOException {
        //JEToBpmnMapper.createBpmnFromJEWorkflow(workflow);
        //TODO fix this shit will u still just testing atm
        /*
         * testing purposes only
         * */
        JEToBpmnMapper.launchBuildTest(workflow);

        HashMap<String, String> wfMap = new HashMap<String, String>();
        wfMap.put("key", workflow.getWorkflowName().trim());
        wfMap.put("path", "processes/" + workflow.getWorkflowName().trim() + ".bpmn");
        wfMap.put("projectId", workflow.getJobEngineProjectID());

        Response response = Network.makeNetworkCallWithJsonBodyWithResponse(wfMap, APIConstants.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_WORKFLOW);
        JELogger.info(WorkflowBuilder.class, response.body().string());

    }


    /*
     * Run workflow in runtime engine
     * */
    public static void runWorkflow(String key) throws IOException {
        Response response = Network.makeNetworkCallWithResponse(APIConstants.RUNTIME_MANAGER_BASE_API + APIConstants.RUN_WORKFLOW + key);
        JELogger.info(WorkflowBuilder.class, response.body().string());

    }


}
