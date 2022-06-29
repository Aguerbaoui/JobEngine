package io.je.utilities.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import utils.network.Network;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Request {
    private Request() {
    }

    /*
     * POST with json
     * */
    static JEResponse sendRequestWithBody(String requestUrl, Object requestBody)
            throws JERunnerErrorException {
        Response response = null;
        try {
            response = Network.makeNetworkCallWithJsonBodyWithResponse(requestBody, requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                        LogSubModule.JEBUILDER, null);*/
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException e) {
            /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                    LogSubModule.JEBUILDER, null);*/
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * PATCH with json
     * */
    static JEResponse sendPatchRequestWithBody(String requestUrl, Object requestBody)
            throws JERunnerErrorException {
        Response response = null;
        try {
            response = Network.makePatchNetworkCallWithJsonBodyWithResponse(requestBody, requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                        LogSubModule.JEBUILDER, null);*/
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException e) {
            /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                    LogSubModule.JEBUILDER, null);*/
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * DELETE with no json
     * */
    static JEResponse sendDeleteRequest(String requestUrl)
            throws JERunnerErrorException {
        Response response = null;
        try {
            response = Network.makeDeleteNetworkCallWithResponse(requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                        LogSubModule.JEBUILDER, null);*/
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException e) {
            /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                    LogSubModule.JEBUILDER, null);*/
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * GET with no body
     * */
    static JEResponse sendRequest(String requestUrl)
            throws JERunnerErrorException {
        Response response = null;
        try {
            response = Network.makeGetNetworkCallWithResponse(requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                        LogSubModule.JEBUILDER, null);*/
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.message());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException e) {
            /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                    LogSubModule.JEBUILDER, null);*/
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * POST with string body
     * */
    static JEResponse sendRequestWithStringBody(String requestUrl, String requestBody)
            throws JERunnerErrorException {
        Response response = null;
        try {
            //JELogger.debug(JERunnerAPIHandler.class, " url = " + requestUrl);
            response = Network.makeNetworkCallWithStringObjectBodyWithResponse(requestBody, requestUrl);
            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
               /* JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                        LogSubModule.JEBUILDER, null);*/
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException e) {
            /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                    LogSubModule.JEBUILDER, null);*/
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * GET with no body
     * */
    static Object sendRequestWithReturnClass(String requestUrl, Class<?> classToCastTo)
            throws JERunnerErrorException {
        Response response = null;
        try {
            response = Network.makeGetNetworkCallWithResponse(requestUrl);

            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                        LogSubModule.JEBUILDER, null);*/
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.message());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, classToCastTo);
        } catch (IOException | InterruptedException | ExecutionException e) {
            /*JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl, LogCategory.RUNTIME, null,
                    LogSubModule.JEBUILDER, null);*/
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

}
