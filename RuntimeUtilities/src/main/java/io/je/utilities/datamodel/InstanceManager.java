package io.je.utilities.datamodel;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.models.InstanceModel;

public class InstanceManager {
	
	static ObjectMapper objectMapper = new ObjectMapper();

	//instanceId/Instance
	static HashMap<String, Object> instancesLastValue = new HashMap<>();
	
	public static Object createInstance(InstanceModel instanceModel ) throws InstanceCreationFailed
	{
		//get instance class
		Class<?> instanceClass = ClassRepository.getClassById(instanceModel.getModelId());
		if(instanceClass == null)
		{
			throw new InstanceCreationFailed("Loaded classes list does not recognize this id :" + instanceModel.getInstanceId());
		}
			
		
		//create instance
		Object instance=null;
		//addInstanceId
		JSONObject instanceJson = instanceModel.getPayload();

		instanceJson.put("jobEngineElementID", instanceModel.getInstanceId());
		//instanceJson.put("jobEngineElementName", instanceModel.getInstanceName());

		try {
			instance = objectMapper.readValue(instanceModel.getPayload().toString(), instanceClass);
		} catch (Exception e) {
			
			e.printStackTrace();
			//TODO: add error msg to config
			throw new InstanceCreationFailed("Failed to create instance : " + e.getMessage());

		}
		instancesLastValue.put(instanceModel.getInstanceId(), instance);
		return instance ;
	}
	
	public static Object getInstance(String instanceId)
	{
		return instancesLastValue.getOrDefault(instanceId, null);
	}

}
