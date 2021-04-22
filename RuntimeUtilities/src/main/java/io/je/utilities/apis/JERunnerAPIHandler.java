package io.je.utilities.apis;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.JEMessages;
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


    private JERunnerAPIHandler() {}

    
    private static String runtimeManagerBaseApi = JEConfiguration.getRuntimeManagerURL();
  
	public static void setRuntimeManagerBaseApi(String runtimeUrl) {
		runtimeManagerBaseApi = runtimeUrl;	
	}

    public static String getRuntimeManagerBaseApi() {
		return runtimeManagerBaseApi;
	}

	/*
    * POST with json
    * */
	private static JEResponse sendRequestWithBody(String requestUrl, Object requestBody)
            throws JERunnerErrorException, InterruptedException, ExecutionException {
        Response response = null;
        try {
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestBody, requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class,
                		 JEMessages.NETWORK_CALL_ERROR + requestUrl);
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body().string());
            }

            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class,
            		 JEMessages.NETWORK_CALL_ERROR + requestUrl);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
        }
    }

    /*
    * DELETE with no json
    * */
    private static JEResponse sendDeleteRequest(String requestUrl)
            throws JERunnerErrorException, InterruptedException, ExecutionException {
        Response response = null;
        try {
            response = Network.makeDeleteNetworkCallWithResponse(requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class,
                		 JEMessages.NETWORK_CALL_ERROR + requestUrl);
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body().string());
            }

            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class,
            		 JEMessages.NETWORK_CALL_ERROR + requestUrl);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
        }
    }

    /*
    * GET with no body
    * */
    private static JEResponse sendRequest(String requestUrl)
            throws JERunnerErrorException, InterruptedException, ExecutionException {
        Response response = null;
        try {
            response = Network.makeGetNetworkCallWithResponse(requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class,
                         JEMessages.NETWORK_CALL_ERROR + requestUrl);
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.message());
            }

            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class,
                     JEMessages.NETWORK_CALL_ERROR + requestUrl);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
        }
    }

    /*
    * POST with string body
    * */
    private static JEResponse sendRequestWithStringBody(String requestUrl, String requestBody)
            throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        Response response = null;
        try {
            //JELogger.debug(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithStringObjectBodyWithResponse(requestBody, requestUrl);
            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class,  JEMessages.NETWORK_CALL_ERROR + requestUrl + " response = " + response.body());
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body().string());
            }

            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class,  JEMessages.NETWORK_CALL_ERROR + requestUrl + " response = " + response.body());
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
        }
    }

    /*
     * run project
     */
    public static JEResponse runProject(String projectId)
            throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.RUN_PROJECT + projectId;
        return sendRequest(requestUrl);
    }

    public static JEResponse stopProject(String projectId)
            throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.STOP_PROJECT + projectId;
        return sendRequest(requestUrl);
    }

    //////// RULES //////////

    // add rule
    public static JEResponse addRule(Object requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.ADD_RULE;
        return sendRequestWithBody(requestUrl, requestModel);

    }

    // compile rule
    public static JEResponse compileRule(HashMap<String, String> requestModel)
            throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.COMPILERULE;
        return sendRequestWithBody(requestUrl, requestModel);
    }

    // update rule
    public static JEResponse updateRule(Object requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.UPDATERULE;
        return sendRequestWithBody(requestUrl, requestModel);
    }

    public static JEResponse deleteRule(String projectId, String ruleId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.DELETERULE + "/" + projectId + "/" + ruleId;
        return sendRequest(requestUrl);
    }

    ///// CLASSES ///////

    public static JEResponse addClass(HashMap<String, String> requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + ADD_CLASS;
        return sendRequestWithBody(requestUrl, requestModel);

    }

    ///////////////////////////////// EVENTS//////////////////////////////

    public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + EVENT_TRIGGER_EVENT + projectId + "/" + eventId;
        return sendRequest(requestUrl);


    }

    // add event
    public static JEResponse addEvent(HashMap<String, String> requestModel) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + EVENT_ADD_EVENT;
        return sendRequestWithBody(requestUrl, requestModel);
    }

    ////////////// Workflows

    // add workflow
    public static JEResponse addWorkflow(WorkflowModel wf) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        Response response = null;
        String requestUrl = runtimeManagerBaseApi + APIConstants.ADD_WORKFLOW;
        return sendRequestWithBody(requestUrl, wf);

    }

    //run workflow
    public static JEResponse runWorkflow(String requestUrl) throws JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        return sendRequest(requestUrl);

    }

    //update event type
    public static void updateEventType(String projectId, String eventId, String type) throws JERunnerErrorException, InterruptedException, ExecutionException, IOException {
        String requestUrl = runtimeManagerBaseApi + APIConstants.UPDATE_EVENT + "/" + projectId + "/" + eventId;
        JELogger.debug(JERunnerAPIHandler.class, JEMessages.NETWORK_UPDATE_EVENT+"project id = " + projectId + "event id = " + eventId);
        sendRequestWithStringBody(requestUrl, type);

    }


    // check runner health
    public static boolean checkRunnerHealth() throws InterruptedException, JERunnerErrorException, ExecutionException, IOException {
        String requestUrl = runtimeManagerBaseApi + ACTUATOR_HEALTH;
        Response response = null;
        try {
            //JELogger.debug(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeGetNetworkCallWithResponse(requestUrl);
            if (response != null) {
                if (response.code() != ResponseCodes.CODE_OK) {
                    JELogger.error(JERunnerAPIHandler.class,
                             JEMessages.NETWORK_CALL_ERROR + requestUrl);
                    throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body().string());
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
                     JEMessages.NETWORK_CALL_ERROR + requestUrl);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
        }
        return false;
    }

    //delete event from runner
    public static JEResponse deleteEvent(String projectId, String eventId) throws InterruptedException, JERunnerErrorException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + DELETE_EVENT + "/" + projectId + "/" + eventId;
        JELogger.debug(JEMessages.NETWORK_DELETE_EVENT+", project id = " + projectId + "event id = " + eventId);
        return sendDeleteRequest(requestUrl);
    }

    // clean project data from runner
    public static void cleanProjectDataFromRunner(String projectId) throws InterruptedException, JERunnerErrorException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + CLEAN_HOUSE + "/" + projectId ;
        JELogger.debug(JERunnerAPIHandler.class, JEMessages.NETWORK_CLEAN_PROJECT+" project id = " + projectId);
        sendRequest(requestUrl);
    }


	public static JEResponse updateRunnerSettings(Object requestModel) throws JERunnerErrorException, InterruptedException, ExecutionException {
	       String requestUrl = runtimeManagerBaseApi + APIConstants.UPDATE_CONFIG ;
	        JELogger.debug(JEMessages.RUNNER_CONFFIG_UPDATE);
	        return sendRequestWithBody(requestUrl, requestModel);

		
	}

    public static void deleteWorkflow(String projectId, String workflowId) throws InterruptedException, JERunnerErrorException, ExecutionException {
        String requestUrl = runtimeManagerBaseApi + DELETE_WORKFLOW + "/" + projectId + "/" + workflowId;
        JELogger.debug(JEMessages.NETWORK_DELETE_WF+" project id = " + projectId + "workflow id = " + workflowId);
         sendDeleteRequest(requestUrl);
    }

    public static void addVariable(String projectId, String varId, Object body) throws InterruptedException, JERunnerErrorException, ExecutionException {
        String url = JEConfiguration.getRuntimeManagerURL()+ APIConstants.ADD_VARIABLE;
        JELogger.debug(JEMessages.NETWORK_ADD_VAR+" project id = " + projectId + "variable id = " + varId);
        sendRequestWithBody(url, body);
    }

    public static void removeVariable(String projectId, String varId) throws InterruptedException, JERunnerErrorException, ExecutionException {
        String url = JEConfiguration.getRuntimeManagerURL()+ DELETE_VARIABLE + "/" + projectId + "/" + varId;
        JELogger.debug(JEMessages.NETWORK_DELETE_VAR+" project id = " + projectId + " var id = " + varId);
        sendDeleteRequest(url);
    }
}
