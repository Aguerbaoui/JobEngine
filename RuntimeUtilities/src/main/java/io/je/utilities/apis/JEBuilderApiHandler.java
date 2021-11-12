package io.je.utilities.apis;

import static io.je.utilities.apis.Request.*;
import static io.je.utilities.constants.APIConstants.*;

import java.util.concurrent.ExecutionException;

import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.log.JELogger;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import io.je.utilities.beans.JEResponse;

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
    public static JEResponse removeWorkflow(String projectId, String workflowId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_WORKFLOW + "/" + projectId + workflowId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, workflowId);*/
		return sendDeleteRequest(requestUrl);
    }

    //Remove rule from project
	public static JEResponse removeRule(String projectId, String ruleId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + "rule/ " + projectId + "/" + "deleteRule" + "/" + ruleId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE+" project id = " + projectId + " rule id = " + ruleId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, ruleId);*/
		return sendDeleteRequest(requestUrl);
	}

	//Remove variable from project
	public static JEResponse removeVariable(String projectId, String variableId) throws InterruptedException, JERunnerErrorException, ExecutionException {
    	String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_VARIABLE + "/" + projectId + "/" + variableId;
		/*.debug(JEMessages.NETWORK_DELETE_VAR+" project id = " + projectId + " var id = " + variableId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, variableId);*/
		return  sendDeleteRequest(requestUrl);
	}

	//Remove event from project
	public static JEResponse removeEvent(String projectId, String eventId) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + DELETE_EVENT + "/" + projectId + "/" + eventId;
		/*JELogger.debug(JEMessages.NETWORK_DELETE_EVENT+", project id = " + projectId + "event id = " + eventId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, eventId);*/
		return sendDeleteRequest(requestUrl);
	}

	//Add variable to project
	public static JEResponse addVariable(String projectId, String varId, Object body) throws InterruptedException, JERunnerErrorException, ExecutionException {
		String url = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder()+ APIConstants.ADD_VARIABLE;
		/*JELogger.debug(JEMessages.NETWORK_ADD_VAR+" project id = " + projectId + " variable id = " + varId, LogCategory.RUNTIME,
				projectId, LogSubModule.JEBUILDER, varId);*/
		return sendRequestWithBody(url, body);
	}
	  public static JEResponse untriggerEvent(String eventId, String projectId) throws JERunnerErrorException, InterruptedException, ExecutionException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + EVENT_UNTRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);
    }
	   public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException, InterruptedException, ExecutionException {
        String requestUrl = SIOTHConfigUtility.getSiothConfig().getJobEngine().getJeBuilder() + EVENT_TRIGGER_EVENT + projectId + "/" + eventId;
		   /*JELogger.debug(JEMessages.TRIGGERING_NOW+" project id = " + projectId + " event id = " + eventId, LogCategory.RUNTIME,
				   projectId, LogSubModule.JEBUILDER, eventId);*/
        return sendRequest(requestUrl);
    }
}
