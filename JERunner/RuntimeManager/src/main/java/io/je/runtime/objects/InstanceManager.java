package io.je.runtime.objects;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.je.runtime.models.InstanceModel;
import io.je.runtime.repos.ClassRepository;
import io.je.utilities.exceptions.InstanceCreationFailed;

public class InstanceManager {
	
	static ObjectMapper objectMapper = new ObjectMapper();

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
		} catch (JsonProcessingException e) {
			
			e.printStackTrace();
			//TODO: add error msg to config
			throw new InstanceCreationFailed("Failed to create instance : " + e.getMessage());

		}
		
		return instance ;
	}

}
