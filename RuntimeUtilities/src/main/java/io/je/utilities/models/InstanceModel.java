package io.je.utilities.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.utilities.mapping.InstanceModelMapping;
import org.json.JSONObject;


public class InstanceModel {

    @JsonProperty(InstanceModelMapping.MODELID)
    String modelId;

    @JsonProperty(InstanceModelMapping.MODELNAME)
    String modelName;

    @JsonProperty(InstanceModelMapping.INSTANCEID)
    String instanceId;

    @JsonProperty(InstanceModelMapping.INSTANCENAME)
    String instanceName;

    @JsonProperty(InstanceModelMapping.ISHDA)
    boolean isHDA;


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


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public boolean isHDA() {
        return isHDA;
    }

    public void setHDA(boolean isHDA) {
        this.isHDA = isHDA;
    }

    @Override
    public String toString() {
        return "InstanceModel [modelId=" + modelId + ", modelName=" + modelName + ", instanceId=" + instanceId
                + ", instanceName=" + instanceName + ", isHDA=" + isHDA + ", payload=" + payload + "]";
    }


}
