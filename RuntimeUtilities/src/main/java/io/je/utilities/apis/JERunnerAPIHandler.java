package io.je.utilities.apis;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.models.WorkflowModel;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static io.je.utilities.constants.APIConstants.*;

/*
 * class that handles interaction with the JERunner REST API
 */
public class JERunnerAPIHandler {



	private static JEResponse sendRequestWithBody(String requestUrl, Object requestBody)
			throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		Response response = null;
		try {
			JELogger.info(JERunnerAPIHandler.class, " url = " + requestUrl);
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestBody, requestUrl);

			if (response.code() != ResponseCodes.CODE_OK) {
				JELogger.error(JERunnerAPIHandler.class,
						"Error making network call for url = " + requestUrl );
				throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody, JEResponse.class);
		} catch (IOException e) {
			JELogger.error(JERunnerAPIHandler.class,
					"Error making network call for url = " + requestUrl );
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
	}

	private static JEResponse sendRequest(String requestUrl)
			throws IOException, JERunnerErrorException, InterruptedException, ExecutionException {
		Response response = null;
		try {
			//JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
			response = Network.makeGetNetworkCallWithResponse(requestUrl);

			if (response.code() != ResponseCodes.CODE_OK) {
				JELogger.error(JERunnerAPIHandler.class,
						"Error making network call for url = " + requestUrl );
				throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody, JEResponse.class);
		} catch (IOException e) {
			JELogger.error(JERunnerAPIHandler.class,
					"Error making network call for url = " + requestUrl );
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
	}

	private static JEResponse sendRequestWithStringBody(String requestUrl, String requestBody)
			throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		Response response = null;
		try {
			//JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
			response = Network.makeNetworkCallWithStringObjectBodyWithResponse(requestBody, requestUrl);


			if (response.code() != ResponseCodes.CODE_OK) {
				JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
				throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody, JEResponse.class);
		} catch (IOException e) {
			JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
	}
	/*
	 * run project
	 */
	public static JEResponse runProject(String projectId)
			throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.RUN_PROJECT + projectId;
		return sendRequest(requestUrl);
	}

	public static JEResponse stopProject(String projectId)
			throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.STOP_PROJECT + projectId;
		return sendRequest(requestUrl);
	}

	//////// RULES //////////

	// add rule
	public static JEResponse addRule(Object requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_RULE;
		return sendRequestWithBody(requestUrl, requestModel);

	}

	// compile rule
	public static JEResponse compileRule(HashMap<String, String> requestModel)
			throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.COMPILERULE;
		return sendRequestWithBody(requestUrl, requestModel);
	}

	// update rule
	public static JEResponse updateRule(Object requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.UPDATERULE;
		return sendRequestWithBody(requestUrl, requestModel);
	}

	public static JEResponse deleteRule(String projectId,String ruleId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.DELETERULE+"/"+projectId+"/"+ruleId;
		return sendRequest(requestUrl);
	}
	
	///// CLASSES ///////

	public static JEResponse addClass(HashMap<String, String> requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + ADD_CLASS;
		return sendRequestWithBody(requestUrl, requestModel);

	}

	///////////////////////////////// EVENTS//////////////////////////////

	public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl =	JEGlobalconfig.RUNTIME_MANAGER_BASE_API + EVENT_TRIGGER_EVENT + projectId + "/" + eventId;
		return sendRequest(requestUrl) ;


	}

	// add event
	public static JEResponse addEvent(HashMap<String, String> requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + EVENT_ADD_EVENT;
		return sendRequestWithBody(requestUrl, requestModel);
	}

	////////////// Workflows

	// add workflow
	public static JEResponse addWorkflow(WorkflowModel wf) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		Response response = null;
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_WORKFLOW;
		return sendRequestWithBody(requestUrl, wf);

	}

	public static JEResponse runWorkflow(String requestUrl) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
		return sendRequest(requestUrl);

	}

    public static JEResponse updateEventType(String projectId, String eventId, String type) throws JERunnerErrorException, InterruptedException, ExecutionException, IOException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.UPDATE_EVENT + "/" + projectId + "/" + eventId;
        JELogger.trace(JERunnerAPIHandler.class, "Sending update event request to runner, project id = " + projectId + "event id = " + eventId);
        return sendRequestWithStringBody(requestUrl, type);

    }


	public static JEResponse requestUpdateFromBuilder() throws InterruptedException, JERunnerErrorException, ExecutionException, IOException {
		String requestUrl = JEGlobalconfig.BUILDER_BASE_API + PROJECT_UPDATE_RUNNER;
		return sendRequest(requestUrl);
	}

	public static boolean checkRunnerHealth() throws InterruptedException, JERunnerErrorException, ExecutionException, IOException {
		String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + ACTUATOR_HEALTH;
		Response response = null;
		try {
			//JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
			response = Network.makeGetNetworkCallWithResponse(requestUrl);
			if(response != null) {
				if (response.code() != ResponseCodes.CODE_OK) {
					JELogger.error(JERunnerAPIHandler.class,
							"Error making network call for url = " + requestUrl);
					throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
				}

				String respBody = response.body().string();
				ObjectMapper objectMapper = new ObjectMapper();
				HashMap<String, String> v = objectMapper.readValue(respBody, HashMap.class);
				if (v.containsKey("status") && v.get("status").equalsIgnoreCase("up")) {
					return true;
				}
			}

		} catch (IOException e) {
			JELogger.error(JERunnerAPIHandler.class,
					"Error making network call for url = " + requestUrl );
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
		return false;
	}
}
