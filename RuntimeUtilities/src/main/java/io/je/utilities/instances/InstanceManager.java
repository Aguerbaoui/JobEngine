package io.je.utilities.instances;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.classloader.JEClassLoader;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.InstanceModelMapping;
import io.je.utilities.models.InstanceModel;
import io.je.utilities.runtimeobject.JEObject;
import utils.log.LogCategory;
import utils.log.LogSubModule;


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
   		instanceModel.setModelName(instanceJson.getString(InstanceModelMapping.MODELNAME));

   		instanceModel.setPayload(instanceJson.getJSONObject(InstanceModelMapping.PAYLOAD));
   		return instanceModel;
	}
	

	
	
	/*
	 * create an instance from an InstanceModel
	 */
	public static JEObject createInstance(String dataReceived ) throws InstanceCreationFailed
	{
		 objectMapper.setTypeFactory(objectMapper.getTypeFactory().withClassLoader(JEClassLoader.getInstance()));
		
		InstanceModel instanceModel = getInstanceModel(dataReceived);
		JELogger.control("Listening for data from class "+instanceModel.getModelName(), null, "", LogSubModule.JERUNNER, "123");

		
		//Retrieve Instance Class
		Class<?> instanceClass = ClassRepository.getClassById(instanceModel.getModelId());
		if(instanceClass == null)
		{
			throw new InstanceCreationFailed(JEMessages.CLASS_NOT_LOADED + instanceModel.getInstanceId());
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
			throw new InstanceCreationFailed(JEMessages.ADD_INSTANCE_FAILED + e.getMessage());

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
        	    List<HashMap<String,Object>> attributes = objectMapper.readValue(dataReceived, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, HashMap.class));
        	    for(HashMap<String, Object> attribute : attributes)
        	    {
        	    	if(attribute.get("attributeName").equals(attributeName))
        	    	{
        	    		return attribute.get("value").toString();
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
    			JELogger.error(JEMessages.READ_INSTANCE_FAILED+ instanceId , null, "", LogSubModule.JERUNNER, instanceId);

    		}
			 
		} catch (Exception e) {
			JELogger.error(JEMessages.READ_INSTANCE_FAILED + instanceId , null, "", LogSubModule.JERUNNER, instanceId);
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
				JELogger.trace(JEMessages.INSTANCE_UPDATE_SUCCESS,  LogCategory.RUNTIME,
	                    null, LogSubModule.RULE, null);
			}else {
				JELogger.error(JEMessages.WRITE_INSTANCE_FAILED +response.getJSONObject("Fail")+". "+JEMessages.CHECK_DM_FOR_DETAILS, LogCategory.RUNTIME, "", LogSubModule.JERUNNER, instanceId);
			}

	

		}else
        {
        	JELogger.error(JEMessages.WRITE_INSTANCE_FAILED ,  LogCategory.RUNTIME,
                    null, LogSubModule.RULE, null);
        }
		
       
		
	}

}
