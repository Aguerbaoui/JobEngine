package io.je.rulebuilder.models;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.config.AttributesMapping;

public class BlockConfigurationModel {
	
	//attribute for Function blocks (power,bias,gain)
	// or comparison Threshold for comparison blocks 
    @JsonProperty(AttributesMapping.VALUE)
	String value;
	
	//unitConversion
    @JsonProperty(AttributesMapping.INPUTUNIT)
	String inputUnit;
    
    @JsonProperty(AttributesMapping.OUTPUTUNIT)
	String outputUnit;
	
	//getter blocks 
    @JsonProperty(AttributesMapping.CLASSID)
	String classId;
    
    @JsonProperty(AttributesMapping.CLASSNAME)
   	String className;
    
    @JsonProperty(AttributesMapping.WORKSPACEID)
   	String workspaceId;
       
    
    @JsonProperty(AttributesMapping.ATTRIBUTENAME)
	String attributeName;
    
    @JsonProperty(AttributesMapping.TYPE)
	String type;

    @JsonProperty(AttributesMapping.SPECIFICINSTANCES)
    List<String> specificInstances;
	
    @JsonProperty(AttributesMapping.INSTANCEID)
    String instanceId;
    
	
	
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getInputUnit() {
		return inputUnit;
	}
	public void setInputUnit(String inputUnit) {
		this.inputUnit = inputUnit;
	}
	public String getOutputUnit() {
		return outputUnit;
	}
	public void setOutputUnit(String outputUnit) {
		this.outputUnit = outputUnit;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getWorkspaceId() {
		return workspaceId;
	}
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	
	
	
	public List<String> getSpecificInstances() {
		return specificInstances;
	}
	public void setSpecificInstances(List<String> specificInstances) {
		this.specificInstances = specificInstances;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	@Override
	public String toString() {
		return "BlockConfigurationModel [value=" + value + ", inputUnit=" + inputUnit + ", outputUnit=" + outputUnit
				+ ", classId=" + classId + ", className=" + className + ", workspaceId=" + workspaceId
				+ ", attributeName=" + attributeName + ", type=" + type + ", specificInstances=" + specificInstances
				+ ", instanceId=" + instanceId + "]";
	}

	
	
	
	
	
	

}
