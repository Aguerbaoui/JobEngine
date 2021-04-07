package io.je.utilities.apis;

import static io.je.utilities.constants.APIConstants.PROJECT_UPDATE_RUNNER;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;

public class JEBuilderApiHandler {
	
	
	  private static JEResponse sendRequest(String requestUrl)
	            throws JERunnerErrorException, InterruptedException, ExecutionException {
	        Response response = null;
	        try {
				JELogger.info(JEMessages.RUNNER_SHUTTING_DOWN);
	            response = Network.makeGetNetworkCallWithResponse(requestUrl);

	            if (response == null) throw new JERunnerErrorException(JEMessages.JEBUILDER_UNREACHABLE);
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
	            throw new JERunnerErrorException(JEMessages.JEBUILDER_UNREACHABLE);
	        }
	    }
	
	
    // request update from builder
    public static void requestUpdateFromBuilder() throws InterruptedException, JERunnerErrorException, ExecutionException {

        String requestUrl = JEConfiguration.getProjectBuilderURL() + PROJECT_UPDATE_RUNNER;
        sendRequest(requestUrl);
    }


}
