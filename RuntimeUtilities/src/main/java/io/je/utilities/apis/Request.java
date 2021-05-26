package io.je.utilities.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Request {
    /*
     * POST with json
     * */
    static JEResponse sendRequestWithBody(String requestUrl, Object requestBody)
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
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * DELETE with no json
     * */
    static JEResponse sendDeleteRequest(String requestUrl)
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
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * GET with no body
     * */
    static JEResponse sendRequest(String requestUrl)
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
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }

    /*
     * POST with string body
     * */
    static JEResponse sendRequestWithStringBody(String requestUrl, String requestBody)
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
            throw new JERunnerErrorException(JEMessages.JERUNNER_UNREACHABLE + " Or " + JEMessages.JEBUILDER_UNREACHABLE);
        }
    }
}
