package io.je.project.services;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.classbuilder.builder.ClassBuilder;
import io.je.classbuilder.models.ClassModel;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.exceptions.AddClassException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;

/*
 * Service class to handle classes
 */
@Service
public class ClassService {

	private List<String> classes = new ArrayList<>();
	String configurationPath = "D:\\myproject2";

	/*
	 * add class
	 */
	public void addClass(String workspaceId, String classId) throws  IOException, DataDefinitionUnreachableException, JERunnerUnreachableException, AddClassException {
		ObjectMapper objectMapper = new ObjectMapper();
		// get class definition from data model (TODO: get ALL workspace classes )
		String requestURL = APIConstants.CLASS_DEFINITION_API + "/Class/" + classId + "/workspace/" + workspaceId;
		Response resp = null;
		try
		{
			resp = Network.makeNetworkCallWithResponse(requestURL );
			
		}catch(ConnectException e )
		{
			throw new DataDefinitionUnreachableException(ClassBuilderErrors.dataDefinitonUnreachable);
		}
		if(resp == null || resp.code() == 404 )
		{
			throw new DataDefinitionUnreachableException(ClassBuilderErrors.dataDefinitonUnreachable);

		}
		String resp1 = resp.body().string();
		
		JELogger.info( getClass(), " >>>>>>>>>>>>>>>>>>> " + resp1);

		// create class model from response
		ClassModel jeClass = objectMapper.readValue(resp1, ClassModel.class);

		// set workspace id
		jeClass.setWorkspaceId(workspaceId);

		String generatedClassPath = ClassBuilder.buildClass(jeClass, configurationPath);

		// TODO: send class to JERunner to be loaded
		HashMap<String, String> classMap = new HashMap<>();
		classMap.put("className", jeClass.getName());
		classMap.put("classPath", generatedClassPath);
		
		Response classResp = null;
		try
		{
			classResp = Network.makeNetworkCallWithJsonBodyWithResponse(classMap,APIConstants.RUNTIME_MANAGER_BASE_API + "/addClass");

			
		}catch(ConnectException e )
		{
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}
		
		if(classResp == null || classResp.code() == 404 )
		{
			throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
		}

		System.out.println(classResp.code());
		String respBody = classResp.body().string();
		System.out.println(respBody);

		io.je.utilities.network.JEResponse jeRunnerResp = objectMapper.readValue(respBody, io.je.utilities.network.JEResponse.class);
		if(jeRunnerResp.getCode()!=0)
		{
			throw new AddClassException(ClassBuilderErrors.classLoadFailed);
		}


	}
}
