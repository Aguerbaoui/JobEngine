package io.je.rulebuilder.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.config.AttributesMapping;

import java.util.List;

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
    
    @JsonProperty("sourceValueType")
	String type;

    @JsonProperty(AttributesMapping.SPECIFICINSTANCES)
    List<String> specificInstances;
	
    @JsonProperty("destinationClassId")
    String destinationClassId;
    
    @JsonProperty(AttributesMapping.OBJECTID)
    String objectId;
    
	
	
	
	
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

	
	
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
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
	
	
	
	public String getDestinationClassId() {
		return destinationClassId;
	}
	public void setDestinationClassId(String destinationClassId) {
		this.destinationClassId = destinationClassId;
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
				+ ", objectId=" + objectId + "]";
	}

	
	
	
	
	
	

}
