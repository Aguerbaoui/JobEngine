package io.je.utilities.apis;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.beans.*;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.VariableModelMapping;
import io.siothconfig.SIOTHConfigUtility;
import utils.log.LogMessage;
import utils.zmq.ZMQRequester;

public class JERunnerRequester {

	static int requesterPort = 59095;

	private static ZMQRequester requester;
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static void init() {
		try {
			requester = new ZMQRequester("tcp://" + SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),
					requesterPort);

		} catch (Exception e) {
		}
	}

	public static JEZMQResponse updateVariable(String projectId, String variableId, Object value,
			boolean ignoreIfSameValue) {
		RunnerRequestObject request = new RunnerRequestObject(RunnerRequestEnum.UPDATE_VARIABLE);
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(VariableModelMapping.VARIABLE_ID, variableId);
		requestParams.put(VariableModelMapping.PROJECT_ID, projectId);
		requestParams.put(VariableModelMapping.VALUE, value);
		requestParams.put(VariableModelMapping.IGNORE_IF_SAME_VALUE, ignoreIfSameValue);
		request.setRequestBody(requestParams);
		return sendRequest(request);
	}

	public static JEZMQResponse informUser(InformModel informModel) {
		RunnerRequestObject request = new RunnerRequestObject(RunnerRequestEnum.INFORM_USER);
		request.setRequestBody(informModel);
		return sendRequest(request);
	}

	public static JEZMQResponse sendLogMessage(LogMessage logMessage) {
		RunnerRequestObject request = new RunnerRequestObject(RunnerRequestEnum.SEND_LOG);
		request.setRequestBody(logMessage);
		return sendRequest(request);
	}

	public static JEZMQResponse triggerEvent(String projectId, String eventName) {
		RunnerRequestObject request = new RunnerRequestObject(RunnerRequestEnum.TRIGGER_EVENT);
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put("eventId", eventName);
		requestParams.put("projectId", projectId);
		request.setRequestBody(requestParams);
		return sendRequest(request);
	}

	public static JEZMQResponse readVariable(String projectId, String variableId) {
		RunnerRequestObject request = new RunnerRequestObject(RunnerRequestEnum.GET_VARIABLE);
		Map<String, Object> requestParams = new HashMap<>();
		requestParams.put(VariableModelMapping.VARIABLE_ID, variableId);
		requestParams.put(VariableModelMapping.PROJECT_ID, projectId);
		request.setRequestBody(requestParams);
		return sendRequest(request);
	}

	private static JEZMQResponse sendRequest(RunnerRequestObject request)
	    {
	    	try {
	    		String response;
	    		if(requester==null) init();
	    		synchronized (requester) {
	    			response = requester.sendRequest(objectMapper.writeValueAsString(request));	
	  	    	}
	    		
				return objectMapper.readValue(response, JEZMQResponse.class);				
	    	}catch (Exception e) {
	    			e.printStackTrace();
					return new JEZMQResponse(ZMQResponseType.FAIL,"Failed to get response from JERunner.");

	    		}
			
	    }

	public static void setRequesterPort(int requesterPort) {
		JERunnerRequester.requesterPort = requesterPort;
	}

}
