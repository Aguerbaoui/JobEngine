package io.je.utilities.apis;

import java.io.IOException;

import com.squareup.okhttp.Response;


import io.je.utilities.config.JEConfiguration;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.ClassLoadException;
import io.je.utilities.exceptions.DataDefinitionUnreachableException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.network.Network;

/*
 * class that handles interaction with the data definition REST API
 */
public  class DataDefinitionApiHandler {
	
	
	
	



	public static String loadClassDefinition(String workspaceId, String classId) throws DataDefinitionUnreachableException, ClassLoadException, IOException
	{
		String requestURL = JEConfiguration.getDataDefinitionURL() + "/Class/" + classId + "/workspace/" + workspaceId;
		JELogger.debug(DataDefinitionApiHandler.class, " [classID = "+classId+"]"+JEMessages.GETTING_CLASS_DEFINITION);

		Response resp = null;
		try {
			resp = Network.makeGetNetworkCallWithResponse(requestURL);

		} catch (Exception e) {
			throw new DataDefinitionUnreachableException(JEMessages.DATA_DEFINITION_API_UNREACHABLE +" : " + e.getMessage());
		}
		if (resp == null ) {
			throw new DataDefinitionUnreachableException(JEMessages.DATA_DEFINITION_API_UNREACHABLE );


		}else if (resp.code() == 204) {
			
			throw new ClassLoadException(JEMessages.DATA_DEFINITION_CLASS_NOT_FOUND + " : [ id = " + classId + " ]");
		}else if ( resp.code() != 200 && resp.code() != 204)
		{
			throw new DataDefinitionUnreachableException(JEMessages.DATA_DEFINITION_API_ERROR +" : " + resp.body().string());

		}
		
		return resp.body().string();
	}

}
