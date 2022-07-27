package io.je.utilities.apis;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;
import okhttp3.Response;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.models.LibModel;
import io.je.utilities.models.VariableModel;
import io.siothconfig.SIOTHConfigUtility;
import utils.network.Network;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.apis.Request.*;
import static io.je.utilities.constants.APIConstants.*;

public class JEBuilderApiHandler {
    /*
     * Most of these methods are deprecated
     * */

    //run workflow
    public static JEResponse runWorkflow(String projectId, String workflowName) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + APIConstants.RUN_WORKFLOW + projectId + "/" + workflowName;
        return sendRequest(requestUrl);

    }

    //Stop workflow
    public static JEResponse stopWorkflow(String projectId, String workflowId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_WORKFLOW + "/" + projectId + "/" + workflowId;
		JELogger.debug(JEMessages.NETWORK_DELETE_WF + " project id = " + projectId + "workflow id = " + workflowId);
        return sendDeleteRequest(requestUrl);
    }

    // request update from builder
    public static JEResponse requestUpdateFromBuilder() throws JERunnerErrorException {

        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + PROJECT_UPDATE_RUNNER;
        return sendRequest(requestUrl);
    }

    // Remove workflow from project
    public static JEResponse removeWorkflow(String projectId, String workflowId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_WORKFLOW + "/" + projectId + workflowId;
		JELogger.debug(JEMessages.NETWORK_DELETE_WF + " project id = " + projectId + "workflow id = " + workflowId);
        return sendDeleteRequest(requestUrl);
    }

    //Remove rule from project
    public static JEResponse removeRule(String projectId, String ruleId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + "rule/ " + projectId + "/" + "deleteRule" + "/" + ruleId;
		JELogger.debug(JEMessages.NETWORK_DELETE + " project id = " + projectId + " rule id = " + ruleId);
        return sendDeleteRequest(requestUrl);
    }

    //Remove variable from project
    public static JEResponse removeVariable(String projectId, String variableId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_VARIABLE + "/" + projectId + "/" + variableId;
        JELogger.debug(JEMessages.NETWORK_DELETE_VAR+" project id = " + projectId + " var id = " + variableId);
        return sendDeleteRequest(requestUrl);
    }

    //Remove event from project
    public static JEResponse removeEvent(String projectId, String eventId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_EVENT + "/" + projectId + "/" + eventId;
		JELogger.debug(JEMessages.NETWORK_DELETE_EVENT + ", project id = " + projectId + "event id = " + eventId);
        return sendDeleteRequest(requestUrl);
    }

    //Add variable to project
    public static JEResponse addVariable(String projectId, String varId, Object body) throws JERunnerErrorException {
        String url = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + APIConstants.ADD_VARIABLE;
		JELogger.debug(JEMessages.NETWORK_ADD_VAR + " project id = " + projectId + " variable id = " + varId);
        return sendRequestWithBody(url, body);
    }

    public static JEResponse untriggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + UNTRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);
    }

    public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + TRIGGER_EVENT + projectId + "/" + eventId;
        JELogger.debug(JEMessages.TRIGGERING_NOW + " project id = " + projectId + " event id = " + eventId);
        return sendRequest(requestUrl);
    }

    public static JEResponse updateWorkflowStatus(String workflowId, String projectId, Object obj) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + UPDATE_WORKFLOW_STATUS;
        JELogger.debug("requestUrl : " + requestUrl);
        return sendPatchRequestWithBody(requestUrl, obj);
    }

    public static JEResponse informUser(Object body) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + INFORM_USER;
        JELogger.debug("requestUrl : " + requestUrl);
        return sendRequestWithBody(requestUrl, body);
    }


    public static JEResponse sendLogMessage(Object body) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + SEND_LOG;
        JELogger.debug("requestUrl : " + requestUrl);
        return sendRequestWithBody(requestUrl, body);
    }

    public static int uploadFileTo(String url, LibModel libModel) throws ExecutionException, InterruptedException, IOException {
        Response response = sendMultipartFormDataPostRequest(url, libModel);
        return response.code();
    }

    private static Response sendMultipartFormDataPostRequest(String url, LibModel libModel) throws ExecutionException, InterruptedException, IOException {
        String fileName = libModel.getFile().getOriginalFilename();
        return Network.makeMultipartFormDataPost(url, fileName, libModel.getFilePath());
    }

    public static VariableModel getVariable(String projectName, String variableName) {
        try {//http://njendoubi-pc:13020/ProjectBuilder//variable/test/getVariable//testVar
            String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder()
                    + "/variable/" + projectName + GET_VARIABLE + "/" + variableName;
            VariableModel variableModel = (VariableModel) sendRequestWithReturnClass(requestUrl, VariableModel.class);
            return variableModel;
			/*Response response = Network.makeGetNetworkCallWithResponse(requestUrl);
			if (response == null || response.code() != ResponseCodes.CODE_OK) return null;
			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody, VariableModel.class);*/
        } catch (Exception exp) {
            JELogger.error(Arrays.toString(exp.getStackTrace()));
            return null;
        }
    }

    public static JEResponse setVariable(String projectName, String variableName, String value) {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder()
                + "/variable/" + projectName + WRITE_TO_VARIABLE + "/" + variableName;
        try {
            return sendRequestWithBody(requestUrl, value);
        } catch (Exception exp) {
            JELogger.error(Arrays.toString(exp.getStackTrace()));
            return null;
        }
    }
}
