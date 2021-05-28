package io.je.runtime.objects;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.je.runtime.models.InstanceModel;
import io.je.runtime.models.InstanceModelMapping;
import io.je.runtime.repos.ClassRepository;
import io.je.utilities.exceptions.InstanceCreationFailed;
import io.je.utilities.runtimeobject.JEObject;

public class test {
	static ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) throws InstanceCreationFailed {

		// TODO Auto-generated method stub
		JSONObject instanceJson = new JSONObject(
				"{\"ModelId\":\"90ed9835-e0a5-e886-409b-caaa96595827\",\"InstanceId\":\"39ed4307-9373-df42-845e-d694e4491d6a\",\"Payload\":{\"TagName\":\"Channel1.Device1.Tag1\",\"Value\":\"28464\",\"TimeStamp\":\"2021-05-28 12:12:29.000\",\"Type\":\"VT_UI2\"}}");
		InstanceModel instanceModel = new InstanceModel();
		instanceModel.setInstanceId(instanceJson.getString(InstanceModelMapping.INSTANCEID));
		instanceModel.setModelId(instanceJson.getString(InstanceModelMapping.MODELID));
		instanceModel.setPayload(instanceJson.getJSONObject(InstanceModelMapping.PAYLOAD));
		// instanceModel.setInstanceName(instanceJson.getString(InstanceModelMapping.INSTANCENAME));

		// get instance class
		Class<?> instanceClass = ClassC.class;
		// create instance
		Object instance = null;
		// addInstanceId
		JSONObject instanceJson1 = instanceModel.getPayload();

		instanceJson1.put("jobEngineElementID", instanceModel.getInstanceId());
		// instanceJson.put("jobEngineElementName", instanceModel.getInstanceName());

		try {
			instance = objectMapper.readValue(instanceModel.getPayload().toString(), instanceClass);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
			// TODO: add error msg to config
			throw new InstanceCreationFailed("Failed to create instance : " + e.getMessage());

		}
	}

}
