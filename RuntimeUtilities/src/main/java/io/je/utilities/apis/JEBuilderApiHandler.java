package io.je.utilities.apis;

import static io.je.utilities.apis.Request.*;
import static io.je.utilities.constants.APIConstants.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;

public class JEBuilderApiHandler {


	//run workflow
	public static JEResponse runWorkflow(String projectId, String workflowName) throws JERunnerErrorException, InterruptedException, ExecutionException {
		String requestUrl = JEConfiguration.getProjectBuilderURL()+ APIConstants.RUN_WORKFLOW + projectId + "/" + workflowName;
		return sendRequest(requestUrl);

	}
	//Stop workflow
	public static JEResponse stopWorkflow(String projectId, String workflowId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = JEConfiguration.getProjectBuilderURL() + DELETE_WORKFLOW + "/" + projectId + "/" + workflowId;
		JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId);
		return sendDeleteRequest(requestUrl);
	}

    // request update from builder
    public static JEResponse requestUpdateFromBuilder() throws InterruptedException, JERunnerErrorException, ExecutionException {

        String requestUrl = JEConfiguration.getProjectBuilderURL() + PROJECT_UPDATE_RUNNER;
        return sendRequest(requestUrl);
    }

    // Remove workflow from project
    public static JEResponse removeWorkflow(String projectId, String workflowId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = JEConfiguration.getProjectBuilderURL() + DELETE_WORKFLOW + "/" + projectId + workflowId;
		JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId);
		return sendDeleteRequest(requestUrl);
    }

    //Remove rule from project
	public static JEResponse removeRule(String projectId, String ruleId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = JEConfiguration.getProjectBuilderURL() + "rule/ " + projectId + "/" + "deleteRule" + "/" + ruleId;
		JELogger.debug(JEMessages.NETWORK_DELETE+" project id = " + projectId + " rule id = " + ruleId);
		return sendDeleteRequest(requestUrl);
	}

	//Remove variable from project
	public static JEResponse removeVariable(String projectId, String variableId) throws InterruptedException, JERunnerErrorException, ExecutionException {
    	String requestUrl = JEConfiguration.getProjectBuilderURL() + DELETE_VARIABLE + "/" + projectId + "/" + variableId;
		JELogger.debug(JEMessages.NETWORK_DELETE_VAR+" project id = " + projectId + " var id = " + variableId);
		return  sendDeleteRequest(requestUrl);
	}

	//Remove event from project
	public static JEResponse removeEvent(String projectId, String eventId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = JEConfiguration.getProjectBuilderURL() + DELETE_EVENT + "/" + projectId + "/" + eventId;
		JELogger.debug(JEMessages.NETWORK_DELETE_EVENT+", project id = " + projectId + "event id = " + eventId);
		return sendDeleteRequest(requestUrl);
	}

	//Add variable to project
	public static JEResponse addVariable(String projectId, String varId, Object body) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String url = JEConfiguration.getProjectBuilderURL()+ APIConstants.ADD_VARIABLE;
		JELogger.debug(JEMessages.NETWORK_ADD_VAR+" project id = " + projectId + " variable id = " + varId);
		return sendRequestWithBody(url, body);
	}
}
