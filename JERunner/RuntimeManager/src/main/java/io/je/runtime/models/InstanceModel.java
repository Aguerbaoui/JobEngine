package io.je.runtime.models;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;


public class InstanceModel {

	@JsonProperty(InstanceModelMapping.MODELID)
	String modelId;
	
	@JsonProperty(InstanceModelMapping.INSTANCEID)
	String instanceId;
	
//	@JsonProperty(InstanceModelMapping.INSTANCENAME)
	//String instanceName;
	
	@JsonProperty(InstanceModelMapping.PAYLOAD)
	JSONObject payload;

	
	
	public InstanceModel() {
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

	

	public JSONObject getPayload() {
		return payload;
	}

	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}



	@Override
	public String toString() {
		return "InstanceModel [modelId=" + modelId + ", instanceId=" + instanceId + ", instanceName="  /* + instanceName*/
				+ ", payload=" + payload + "]";
	}
/*
	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

*/

	
		
	
}
