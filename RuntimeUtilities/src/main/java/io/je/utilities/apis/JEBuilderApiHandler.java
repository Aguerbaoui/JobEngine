package io.je.utilities.apis;

import com.squareup.okhttp.Response;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.models.LibModel;
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
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder()+ APIConstants.RUN_WORKFLOW + projectId + "/" + workflowName;
		return sendRequest(requestUrl);

	}
	//Stop workflow
	public static JEResponse stopWorkflow(String projectId, String workflowId) throws JERunnerErrorException {
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_WORKFLOW + "/" + projectId + "/" + workflowId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, workflowId);*/
		return sendDeleteRequest(requestUrl);
	}

    // request update from builder
    public static JEResponse requestUpdateFromBuilder() throws JERunnerErrorException {

        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + PROJECT_UPDATE_RUNNER;
        return sendRequest(requestUrl);
    }

    // Remove workflow from project
    public static JEResponse removeWorkflow(String projectId, String workflowId) throws  JERunnerErrorException{
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_WORKFLOW + "/" + projectId + workflowId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, workflowId);*/
		return sendDeleteRequest(requestUrl);
    }

    //Remove rule from project
	public static JEResponse removeRule(String projectId, String ruleId) throws  JERunnerErrorException{
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + "rule/ " + projectId + "/" + "deleteRule" + "/" + ruleId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE+" project id = " + projectId + " rule id = " + ruleId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, ruleId);*/
		return sendDeleteRequest(requestUrl);
	}

	//Remove variable from project
	public static JEResponse removeVariable(String projectId, String variableId) throws  JERunnerErrorException{
    	String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_VARIABLE + "/" + projectId + "/" + variableId;
		/*.debug(JEMessages.NETWORK_DELETE_VAR+" project id = " + projectId + " var id = " + variableId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, variableId);*/
		return  sendDeleteRequest(requestUrl);
	}

	//Remove event from project
	public static JEResponse removeEvent(String projectId, String eventId) throws  JERunnerErrorException {
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_EVENT + "/" + projectId + "/" + eventId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_EVENT+", project id = " + projectId + "event id = " + eventId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, eventId);*/
		return sendDeleteRequest(requestUrl);
	}

	//Add variable to project
	public static JEResponse addVariable(String projectId, String varId, Object body) throws  JERunnerErrorException{
		String url = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder()+ APIConstants.ADD_VARIABLE;
		/*JELogger.debug(JEMessages.NETWORK_ADD_VAR+" project id = " + projectId + " variable id = " + varId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, varId);*/
		return sendRequestWithBody(url, body);
	}
	  public static JEResponse untriggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + EVENT_UNTRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);
    }
	   public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + EVENT_TRIGGER_EVENT + projectId + "/" + eventId;
		   /*JELogger.debug(JEMessages.TRIGGERING_NOW+" project id = " + projectId + " event id = " + eventId, LogCategory.RUNTIME,
				   projectId, LogSubModule.JEBUILDER, eventId);*/
        return sendRequest(requestUrl);
    }

	public static JEResponse updateWorkflowStatus(String workflowId, String projectId, Object obj) throws JERunnerErrorException {
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + UPDATE_WORKFLOW_STATUS;
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
}
