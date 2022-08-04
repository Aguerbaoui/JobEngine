package io.je.utilities.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.log.JELogger;
import okhttp3.Response;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.network.Network;

import java.io.IOException;
import java.util.Arrays;
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
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException exp) {
            JELogger.logException(exp);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl + " : " + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
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
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException exp) {
            JELogger.logException(exp);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl + " : " + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
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
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException exp) {
            JELogger.logException(exp);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl + " : " + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
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
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.message());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException exp) {
            JELogger.logException(exp);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl + " : " + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    /*
     * POST with string body
     * */
    static JEResponse sendRequestWithStringBody(String requestUrl, String requestBody)
            throws JERunnerErrorException {
        Response response = null;
        try {
            JELogger.debug("Request url = " + requestUrl);
            response = Network.makeNetworkCallWithStringObjectBodyWithResponse(requestBody, requestUrl);
            if (response == null) throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE);
            if (response.code() != ResponseCodes.CODE_OK) {
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.body()
                        .string());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, JEResponse.class);
        } catch (IOException | InterruptedException | ExecutionException exp) {
            JELogger.logException(exp);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl + " : " + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
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
                throw new JERunnerErrorException(JEMessages.JERUNNER_ERROR + " : " + response.message());
            }

            String respBody = response.body()
                    .string();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(respBody, classToCastTo);
        } catch (IOException | InterruptedException | ExecutionException exp) {
            JELogger.logException(exp);
            JELogger.error(JEMessages.NETWORK_CALL_ERROR + requestUrl + " : " + exp.getMessage(),
                    LogCategory.RUNTIME, null, LogSubModule.JEBUILDER, null);
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

}
