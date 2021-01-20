package io.je.utilities.apis;


import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;


public class JERunnerAPIHandler {
	
	
	public static JEResponse runProject(String projectId) throws JERunnerUnreachableException, IOException
	{
		Response response = null;
		try {
			String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.RUN_PROJECT + projectId;
			response = Network.makeGetNetworkCallWithResponse(requestUrl);

		} catch (Exception e) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		if ( response.code() != 200) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		JEResponse jeRunnerResp = objectMapper.readValue(respBody,
				JEResponse.class);
		return jeRunnerResp;
	}
	
	public static JEResponse stopProject(String projectId) throws JERunnerUnreachableException, IOException {
		Response response = null;
		try {
			String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.STOP_PROJECT + projectId;
			response = Network.makeGetNetworkCallWithResponse(requestUrl);

		} catch (Exception e) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		if ( response.code() != 200) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable + " : " + response);
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		JEResponse jeRunnerResp = objectMapper.readValue(respBody,
				JEResponse.class);
		return jeRunnerResp;
	}
	
	
	//////// RULES //////////
	
	
	//add rule 	
	public static JEResponse addRule(HashMap<String,String> requestModel) throws JERunnerUnreachableException, IOException
	{
		Response response = null;
		try {
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
					JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_RULE);

		} catch (Exception e) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		if ( response.code() != 200) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		JEResponse jeRunnerResp = objectMapper.readValue(respBody,
				JEResponse.class);
		return jeRunnerResp;

	}
	
	//compile rule 
	public static JEResponse compileRule(HashMap<String,String> requestModel) throws JERunnerUnreachableException, IOException
	{
		Response response = null;
		try {
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
					JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.COMPILERULE);

		} catch (Exception e) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		if ( response.code() != 200) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		JEResponse jeRunnerResp = objectMapper.readValue(respBody,
				JEResponse.class);
		return jeRunnerResp;

	}
	
	//update rule 
	public static JEResponse updateRule(HashMap<String,String> requestModel) throws JERunnerUnreachableException, IOException
		{
			Response response = null;
			try {
				response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
						JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.UPDATERULE);

			} catch (Exception e) {
				throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
			}

			if ( response.code() != 200) {
				throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody,
					JEResponse.class);
		

		}

	
	///// CLASSES ///////
	
	public static JEResponse addClass(HashMap<String,String> requestModel) throws JERunnerUnreachableException, IOException
	{
		Response response = null;
		try {
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,JEGlobalconfig.RUNTIME_MANAGER_BASE_API + "/addClass");

		} catch (Exception e) {
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable +" "+ e.getMessage());
		}
		if(response == null)
		{
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);

		}
		if ( response.code() != 200) {
			throw new JERunnerUnreachableException("JERunner Unexpected Error : " + response.body());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);
		

	}




}
