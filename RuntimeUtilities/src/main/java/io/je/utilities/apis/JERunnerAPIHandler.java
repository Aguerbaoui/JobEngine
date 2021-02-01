package io.je.utilities.apis;


import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.constants.Errors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.models.EventModel;
import io.je.utilities.network.JEResponse;
import io.je.utilities.network.Network;

/*
 * class that handles interaction with the JERunner REST API
 */
public class JERunnerAPIHandler {
	
	/*
	 * run project
	 */
	public static JEResponse runProject(String projectId) throws JERunnerErrorException, IOException
	{
		Response response = null;
		try {
			String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.RUN_PROJECT + projectId;
			response = Network.makeGetNetworkCallWithResponse(requestUrl);

		} catch (Exception e) {
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
		if(response == null)
		{
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

		}

		if ( response.code() != 200) {
			throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);
	}
	

	
	public static JEResponse stopProject(String projectId) throws JERunnerErrorException, IOException {
		Response response = null;
		try {
			String requestUrl = JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.STOP_PROJECT + projectId;
			response = Network.makeGetNetworkCallWithResponse(requestUrl);

		} catch (Exception e) {
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
		if(response == null)
		{
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

		}

		if ( response.code() != 200) {
			throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);
	
	}
	
	
	//////// RULES //////////
	
	
	//add rule 	
	public static JEResponse addRule(Object requestModel) throws JERunnerErrorException, IOException
	{
		Response response = null;
		try {
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
					JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.ADD_RULE);

		} catch (Exception e) {
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
		if(response == null)
		{
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

		}

		if ( response.code() != 200) {
			throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);
	

	}
	
	//compile rule 
	public static JEResponse compileRule(HashMap<String,String> requestModel) throws JERunnerErrorException, IOException
	{
		Response response = null;
		try {
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
					JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.COMPILERULE);

		} catch (Exception e) {
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
		}
		if(response == null)
		{
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

		}

		if ( response.code() != 200) {
			throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);
	}
	
	//update rule 
	public static JEResponse updateRule(Object requestModel) throws JERunnerErrorException, IOException
		{
			Response response = null;
			try {
				response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
						JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.UPDATERULE);

			} catch (Exception e) {
				throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
			}
			if(response == null)
			{
				throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

			}

			if ( response.code() != 200) {
				throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody,JEResponse.class);
		}

	
	///// CLASSES ///////
	
	public static JEResponse addClass(HashMap<String,String> requestModel) throws JERunnerErrorException, IOException
	{
		Response response = null;
		try {
			response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,JEGlobalconfig.RUNTIME_MANAGER_BASE_API + "/addClass");

		} catch (Exception e) {
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE +" "+ e.getMessage());
		}
		if(response == null)
		{
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

		}
		if ( response.code() != 200) {
			throw new JERunnerErrorException("JERunner Unexpected Error : " + response.body().toString());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);
		

	}

	/////////////////////////////////EVENTS//////////////////////////////

	public static JEResponse triggerEvent(String eventId, String projectId) throws JERunnerErrorException, IOException
	{
		Response response = null;
		try {
			response = Network.makeGetNetworkCallWithResponse(JEGlobalconfig.RUNTIME_MANAGER_BASE_API + "/project/triggerEvent/" + projectId + "/" + eventId);

		} catch (Exception e) {
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE +" "+ e.getMessage());
		}
		if(response == null)
		{
			throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

		}
		if ( response.code() != 200) {
			throw new JERunnerErrorException("JERunner Unexpected Error : " + response.body().toString());
		}

		String respBody = response.body().string();
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(respBody,JEResponse.class);


	}



	//compile rule 
		public static JEResponse addEvent(HashMap<String,String> requestModel) throws JERunnerErrorException, IOException
		{
			Response response = null;
			try {
				response = Network.makeNetworkCallWithJsonBodyWithResponse(requestModel,
						JEGlobalconfig.RUNTIME_MANAGER_BASE_API + "/event/addEvent");

			} catch (Exception e) {
				throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);
			}
			if(response == null)
			{
				throw new JERunnerErrorException(Errors.JERUNNER_UNREACHABLE);

			}

			if ( response.code() != 200) {
				throw new JERunnerErrorException(Errors.JERUNNER_ERROR + " : " + response.body().string());
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(respBody,JEResponse.class);
		}

}
