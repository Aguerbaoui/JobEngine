package io.je.utilities.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.models.LibModel;
import io.je.utilities.models.VariableModel;
import io.siothconfig.SIOTHConfigUtility;
import utils.network.Network;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import java.util.HashMap;

import static io.je.utilities.apis.Request.*;
import static io.je.utilities.constants.APIConstants.*;

public class JEBuilderApiHandler {


	//run workflow
	public static JEResponse runWorkflow(String projectId, String workflowName) throws JERunnerErrorException{
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + APIConstants.RUN_WORKFLOW + projectId + "/" + workflowName;
		return sendRequest(requestUrl);

	}
	//Stop workflow
	public static JEResponse stopWorkflow(String projectId, String workflowId) throws JERunnerErrorException {
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + DELETE_WORKFLOW + "/" + projectId + "/" + workflowId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, workflowId);*/
		return sendDeleteRequest(requestUrl);
	}

    // request update from builder
    public static JEResponse requestUpdateFromBuilder() throws JERunnerErrorException {

        String requestUrl = JEConfiguration.getProjectBuilderUrl() + PROJECT_UPDATE_RUNNER;
        return sendRequest(requestUrl);
    }

    // Remove workflow from project
    public static JEResponse removeWorkflow(String projectId, String workflowId) throws  JERunnerErrorException{
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + DELETE_WORKFLOW + "/" + projectId + workflowId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, workflowId);*/
		return sendDeleteRequest(requestUrl);
    }

    //Remove rule from project
	public static JEResponse removeRule(String projectId, String ruleId) throws  JERunnerErrorException{
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + "rule/ " + projectId + "/" + "deleteRule" + "/" + ruleId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE+" project id = " + projectId + " rule id = " + ruleId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, ruleId);*/
		return sendDeleteRequest(requestUrl);
	}

	//Remove variable from project
	public static JEResponse removeVariable(String projectId, String variableId) throws  JERunnerErrorException{
    	String requestUrl = JEConfiguration.getProjectBuilderUrl() + DELETE_VARIABLE + "/" + projectId + "/" + variableId;
		/*.debug(JEMessages.NETWORK_DELETE_VAR+" project id = " + projectId + " var id = " + variableId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, variableId);*/
		return  sendDeleteRequest(requestUrl);
	}

	//Remove event from project
	public static JEResponse removeEvent(String projectId, String eventId) throws  JERunnerErrorException {
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + DELETE_EVENT + "/" + projectId + "/" + eventId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_EVENT+", project id = " + projectId + "event id = " + eventId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, eventId);*/
		return sendDeleteRequest(requestUrl);
	}

	//Add variable to project
	public static JEResponse addVariable(String projectName, String varId, Object body) throws  JERunnerErrorException{
		String url = JEConfiguration.getProjectBuilderUrl()+ APIConstants.ADD_VARIABLE;
		/*JELogger.debug(JEMessages.NETWORK_ADD_VAR+" project id = " + projectId + " variable id = " + varId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, varId);*/
		return sendRequestWithBody(url, body);
	}
	  public static JEResponse untriggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = JEConfiguration.getProjectBuilderUrl() + EVENT_UNTRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);
    }
	   public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = JEConfiguration.getProjectBuilderUrl() + EVENT_TRIGGER_EVENT + projectId + "/" + eventId;
		   /*JELogger.debug(JEMessages.TRIGGERING_NOW+" project id = " + projectId + " event id = " + eventId, LogCategory.RUNTIME,
				   projectId, LogSubModule.JEBUILDER, eventId);*/
        return sendRequest(requestUrl);
    }

	public static JEResponse updateWorkflowStatus(String workflowId, String projectId, Object obj) throws JERunnerErrorException {
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + UPDATE_WORKFLOW_STATUS;
		return sendPatchRequestWithBody(requestUrl,obj);
	}

	public static JEResponse informUser(Object body) throws JERunnerErrorException {
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + INFORM_USER;
		//System.out.println(requestUrl);
		return sendRequestWithBody(requestUrl, body);
	}


	public static JEResponse sendLogMessage(Object body) throws JERunnerErrorException {
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + SEND_LOG;
		//System.out.println(requestUrl);
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
			String requestUrl = JEConfiguration.getProjectBuilderUrl() + "/variable/" + projectName + GET_VARIABLE + "/" + variableName;
			VariableModel variableModel = (VariableModel) sendRequestWithReturnClass(requestUrl, VariableModel.class);
			return variableModel;
			/*Response response = Network.makeGetNetworkCallWithResponse(requestUrl);
			if (response == null || response.code() != ResponseCodes.CODE_OK) return null;
			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody, VariableModel.class);*/
		}
		catch (Exception e) {return null; }
	}

	public static JEResponse setVariable(String projectName, String variableName, String value) {
		String requestUrl = JEConfiguration.getProjectBuilderUrl() + "/variable/" + projectName + WRITE_TO_VARIABLE + "/" + variableName;
		try {
			return sendRequestWithBody(requestUrl, value);
		}
		catch (Exception e ) {return null;}
	}
}
