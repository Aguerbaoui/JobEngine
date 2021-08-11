package io.je.rulebuilder.models;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.config.AttributesMapping;

public class BlockConfigurationModel {
	
	//attribute for Function blocks (power,bias,gain)
	// or comparison Threshold for comparison blocks 

   	@JsonProperty("newValue")
	Object newValue;	    
	
    @JsonProperty("linkedGetterName")
   	String linkedGetterName;
       
   	
    @JsonProperty(AttributesMapping.VALUE)
	String value;
    
    @JsonProperty(AttributesMapping.VALUE2)
	String value2;

    @JsonProperty(AttributesMapping.BOOLEANVALUE)
    String booleanValue;
	
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
    
    @JsonProperty("destinationAttributeName")
 	String destinationAttributeName;
    
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
	
	
	
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	
	
	
	
	public String getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(String booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	
	
	public String getLinkedGetterName() {
		return linkedGetterName;
	}
	public void setLinkedGetterName(String linkedGetterName) {
		this.linkedGetterName = linkedGetterName;
	}
	public Object getNewValue() {
		return newValue;
	}
	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
	
	
	
	public String getDestinationAttributeName() {
		return destinationAttributeName;
	}
	public void setDestinationAttributeName(String destinationAttributeName) {
		this.destinationAttributeName = destinationAttributeName;
	}
	@Override
	public String toString() {
		return "BlockConfigurationModel [newValue=" + newValue + ", value=" + value + ", value2=" + value2
				+ ", booleanValue=" + booleanValue + ", inputUnit=" + inputUnit + ", outputUnit=" + outputUnit
				+ ", classId=" + classId + ", className=" + className + ", workspaceId=" + workspaceId
				+ ", attributeName=" + attributeName + ", type=" + type + ", specificInstances=" + specificInstances
				+ ", instanceId=" + instanceId + "]";
	}

	
	
	
	
	
	

}
