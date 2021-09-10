package io.je.utilities.instances;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.mapping.InstanceModelMapping;
import io.je.utilities.models.InstanceModel;
import io.je.utilities.runtimeobject.JEObject;


/*
 * Class responsible for handling data model instances
 */
public class InstanceManager {
	
	static ObjectMapper objectMapper = new ObjectMapper();

	//instanceId/Instance
	static ConcurrentHashMap<String, JEObject> instancesLastValue = new ConcurrentHashMap<>();
	
	private static InstanceModel getInstanceModel(String dataReceived ) {
		JSONObject instanceJson = new JSONObject(dataReceived);
   		InstanceModel instanceModel = new InstanceModel();
   		instanceModel.setInstanceId(instanceJson.getString(InstanceModelMapping.INSTANCEID));
   		instanceModel.setModelId(instanceJson.getString(InstanceModelMapping.MODELID));
   		instanceModel.setPayload(instanceJson.getJSONObject(InstanceModelMapping.PAYLOAD));
   		return instanceModel;
	}
	

	
	
	/*
	 * create an instance from an InstanceModel
	 */
	public static JEObject createInstance(String dataReceived ) throws InstanceCreationFailed
	{
		
		
		InstanceModel instanceModel = getInstanceModel(dataReceived);
		
		
		//Retrieve Instance Class
		Class<?> instanceClass = ClassRepository.getClassById(instanceModel.getModelId());
		if(instanceClass == null)
		{
			throw new InstanceCreationFailed("Loaded classes list does not recognize this id :" + instanceModel.getInstanceId());
		}
			
		
		//create instance
		Object instance=null;
		//addInstanceId
		JSONObject payload = instanceModel.getPayload();

		payload.put("jobEngineElementID", instanceModel.getInstanceId());
		//instanceJson.put("jobEngineElementName", instanceModel.getInstanceName());

		
		try {
			instance = objectMapper.readValue(instanceModel.getPayload().toString(), instanceClass);
		} catch (Exception e) {
			
			e.printStackTrace();
			//TODO: add error msg to config
			throw new InstanceCreationFailed("Failed to create instance : " + e.getMessage());

		}
		instancesLastValue.put(instanceModel.getInstanceId(), (JEObject) instance);
		return (JEObject) instance ;
	}
	
	
	
	public static String getAttributeValue(String instanceId,String attributeName)
	{
		try {

    		String dataReceived = DataModelRequester.getLastInstanceValue(instanceId);
    		
    		if(dataReceived!=null)
    		{
    			JSONArray attributes = new JSONArray(dataReceived);
    			for(Object attribute : attributes)
    			{
    				try {
    					JSONObject instanceJson = new JSONObject(attribute);
    	    			if(instanceJson.getString("attributeName").equals(attributeName))
    	    			{
    	    				return instanceJson.getString("value");
    	    			}
    				}catch (Exception e) {
						
					}
    			}
    			
    	   		
    		}else {
    			JELogger.error("Failed to read last values for topic : " + instanceId , null, "", LogSubModule.JERUNNER, instanceId);

    		}
			 
		} catch (Exception e) {
			JELogger.error("Failed to read last values for topic : " + instanceId , null, "", LogSubModule.JERUNNER, instanceId);
		}
		return null;
		
	}
	
	
	public static JEObject getInstance(String instanceId)
	{
		return instancesLastValue.contains(instanceId)? 
				instancesLastValue.get(instanceId):getInstanceValueFromDataModel(instanceId);
		
	}
	
	
	
	
	private static JEObject getInstanceValueFromDataModel(String instanceId)
	{

		try {

    		String data = DataModelRequester.getLastInstanceValue(instanceId);
    		
    		if(data!=null && !data.isEmpty())
    		{
    			return createInstance(data);
    		}else {
    			JELogger.error("Failed to read last values for topic : " + instanceId , null, "", LogSubModule.JERUNNER, instanceId);

    		}
			 
		} catch (Exception e) {
			JELogger.error("Failed to read last values for topic : " + instanceId , null, "", LogSubModule.JERUNNER, instanceId);
		}
		return null;
	}




	public static void writeToDataModelInstance(String instanceId, String attributeName, Object attribueValue) {
		String dataReceived = DataModelRequester .writeToInstance(instanceId, attributeName, attribueValue);
		if(dataReceived!=null && !dataReceived.isEmpty())
		{
			
			JSONObject response = new JSONObject(dataReceived);
			JELogger.trace("Data Model responded with : "+response,  LogCategory.RUNTIME,
                    null, LogSubModule.RULE, null);
			if(!response.getJSONArray("Success").isEmpty())
			{
				JELogger.trace("Instance was updated successfully",  LogCategory.RUNTIME,
	                    null, LogSubModule.RULE, null);
			}

	

		}else
        {
        	JELogger.error(JEMessages.WRITE_INSTANCE_FAILED ,  LogCategory.RUNTIME,
                    null, LogSubModule.RULE, null);
        }
		
       
		
	}

}
