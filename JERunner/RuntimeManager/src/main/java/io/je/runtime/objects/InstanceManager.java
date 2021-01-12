package io.je.runtime.objects;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.je.runtime.models.InstanceModel;

public class InstanceManager {
	
	static ObjectMapper objectMapper = new ObjectMapper();

	public static Object createInstance(InstanceModel instanceModel )
	{
		//get instance class
		Class<?> instanceClass = ClassManager.getClassById(instanceModel.getModelId());
		
		//create instance
		Object instance=null;
		try {
			instance = objectMapper.readValue(instanceModel.getPayload().toString(), instanceClass);
		} catch (JsonProcessingException e) {
			
			e.printStackTrace();
		}
		
		return instance ;
	}

}
