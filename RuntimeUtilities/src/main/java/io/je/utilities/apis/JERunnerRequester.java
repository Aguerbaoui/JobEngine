package io.je.utilities.apis;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.beans.RunnerRequestEnum;
import io.je.utilities.beans.RunnerRequestObject;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.VariableModelMapping;
import io.siothconfig.SIOTHConfigUtility;
import utils.zmq.ZMQRequester;

public class JERunnerRequester {

		
		static int requesterPort=59095;
		
	
	    private static ZMQRequester requester;
	    private static ObjectMapper objectMapper = new ObjectMapper();
	    
	    private static void init()
	    {
	    	try {
		    	requester = new ZMQRequester("tcp://" +SIOTHConfigUtility.getSiothConfig().getNodes().getSiothMasterNode(),requesterPort );

	    	}catch (Exception e) {
			}
	    }
	    
	    
	    public static boolean updateVariable(String projectId,String variableId, Object value, boolean ignoreIfSameValue)
	    {
	    	RunnerRequestObject request = new RunnerRequestObject(RunnerRequestEnum.UPDATE_VARIABLE);
	    	Map<String,Object> requestParams= new HashMap<>();
	    	requestParams.put(VariableModelMapping.VARIABLE_ID,variableId);
	    	requestParams.put(VariableModelMapping.PROJECT_ID, projectId);
	    	requestParams.put(VariableModelMapping.VALUE, value);
	    	requestParams.put(VariableModelMapping.IGNORE_IF_SAME_VALUE, ignoreIfSameValue);
	    	request.setRequestBody(requestParams);
	    	return sendRequest(request);
	    }
	    
	    
	    private static boolean sendRequest(RunnerRequestObject request)
	    {
	    	try {
    			if(requester==null) init();
	    		synchronized (requester) {
				requester.sendRequest(objectMapper.writeValueAsString(request));	
				}
	    		
	    		return true;
	    	}catch (Exception e) {
				return false;
			}
	    }


		public static void setRequesterPort(int requesterPort) {
			JERunnerRequester.requesterPort = requesterPort;
		}
	    
	    
	    
}
