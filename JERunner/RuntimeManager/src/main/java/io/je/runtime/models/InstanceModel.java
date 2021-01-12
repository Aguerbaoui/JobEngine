package io.je.runtime.models;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.runtime.config.InstanceModelMapping;


public class InstanceModel {

	@JsonProperty(InstanceModelMapping.MODELID)
	String modelId;
	
	@JsonProperty(InstanceModelMapping.INSTANCEID)
	String instanceId;
	
	@JsonProperty(InstanceModelMapping.PAYLOAD)
	String payload;

	
	
	private InstanceModel() {
		// TODO Auto-generated constructor stub
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "InstanceModel [modelId=" + modelId + ", instanceId=" + instanceId + ", payload=" + payload + "]";
	}



	
		
	
}
