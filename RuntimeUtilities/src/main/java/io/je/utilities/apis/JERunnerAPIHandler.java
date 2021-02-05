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

/*
 * class that handles interaction with the JERunner REST API
 */
public class JERunnerAPIHandler {

    /*
     * run project
     */
    public static JEResponse runProject(String projectId) throws JERunnerErrorException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.RUN_PROJECT + projectId;
        JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
        try {
            response = Network.makeGetNetworkCallWithResponse(requestUrl);
            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
                throw new JERunnerErrorException(Errors.JERUNNER_ERROR);
            }
            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
            throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
        }
    }


    public static JEResponse stopProject(String projectId) throws JERunnerErrorException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.STOP_PROJECT + projectId;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeGetNetworkCallWithResponse(requestUrl);
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


    //////// RULES //////////


    //add rule
    public static JEResponse addRule(Object requestModel) throws JERunnerErrorException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_RULE;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel, requestUrl);

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

    //compile rule
    public static JEResponse compileRule(HashMap<String, String> requestModel) throws JERunnerErrorException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.COMPILERULE;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel, requestUrl
            );


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

    //update rule
    public static JEResponse updateRule(Object requestModel) throws JERunnerErrorException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.UPDATERULE;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
                    requestUrl);


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


    ///// CLASSES ///////

    public static JEResponse addClass(HashMap<String, String> requestModel) throws JERunnerErrorException{
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_CLASS;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel, requestUrl);

            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
                throw new JERunnerErrorException("JERunner Unexpected Error : " + response.body().toString());
            }

            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
            throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
        }
    }

    /////////////////////////////////EVENTS//////////////////////////////

    public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException, IOException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.TRIGGER_EVENT + projectId + "/" + eventId;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeGetNetworkCallWithResponse(requestUrl);

            if (response.code() != ResponseCodes.CODE_OK) {
                JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
                throw new JERunnerErrorException("JERunner Unexpected Error : " + response.body().toString());
            }

            String respBody = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException e) {
            JELogger.error(JERunnerAPIHandler.class, "Error making network call for url = " + requestUrl + " response = " + response.body());
            throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
        }

    }


    //add event
    public static JEResponse addEvent(HashMap<String, String> requestModel) throws JERunnerErrorException, IOException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_EVENT;
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_EVENT);
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
                    JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_EVENT);


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

    //////////////Workflows

    //add workflow
    public static JEResponse addWorkflow(WorkflowModel wf) throws JERunnerErrorException, IOException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_WORKFLOW;
        JELogger.trace(JERunnerAPIHandler.class, "Sending workflow build request to runner, project id = " + wf.getProjectId() + "wf id = " + wf.getKey());
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithJsonObjectBodyWithResponse(wf, requestUrl);


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

    public static JEResponse updateEventType(String projectId, String eventId, String type) throws JERunnerErrorException {
        Response response = null;
        String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.UPDATE_EVENT + "/" + projectId + "/" + eventId;
        JELogger.trace(JERunnerAPIHandler.class, "Sending update event request to runner, project id = " + projectId + "event id = " + eventId);
        try {
            JELogger.trace(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithJsonObjectBodyWithResponse(type, requestUrl);


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
}
