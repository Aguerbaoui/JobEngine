package io.je.runtime.objects;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.runtime.models.InstanceModel;
import io.je.utilities.exceptions.InstanceCreationFailed;

public class InstanceManager {
	
	static ObjectMapper objectMapper = new ObjectMapper();

	public static Object createInstance(InstanceModel instanceModel ) throws InstanceCreationFailed
	{
		//get instance class
		Class<?> instanceClass = ClassManager.getClassById(instanceModel.getModelId());
		if(instanceClass == null)
		{
			throw new InstanceCreationFailed("UNKNOWN TYPE :" + instanceModel.getInstanceId());
		}
			
		
		//create instance
		Object instance=null;
		//addInstanceId
		JSONObject instanceJson = instanceModel.getPayload();
		instanceJson.put("jobEngineElementId", instanceModel.getInstanceId());
		
		try {
			instance = objectMapper.readValue(instanceModel.getPayload().toString(), instanceClass);
		} catch (JsonProcessingException e) {
			
			e.printStackTrace();
			//TODO: add error msg to config
			throw new InstanceCreationFailed("Failed to create instance : " + e.getMessage());

		}
		
		return instance ;
	}

}
