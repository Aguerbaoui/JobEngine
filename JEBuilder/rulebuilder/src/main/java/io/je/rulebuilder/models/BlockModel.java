package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.config.BlockAttributesMapping;

public class BlockModel {
	
	@JsonProperty(BlockAttributesMapping.PROJECTID)
	String projectId;
	
	@JsonProperty(BlockAttributesMapping.RULEID)
	String ruleId;
	
	@JsonProperty(BlockAttributesMapping.BLOCKID)
	String blockId;
	
	@JsonProperty(BlockAttributesMapping.BLOCKTYPE)
	String blockType;
	
	@JsonProperty(BlockAttributesMapping.TIMEPERSISTENCEON)
	String timePersistenceOn;
	
	@JsonProperty(BlockAttributesMapping.TIMEPERSISTENCEVALUE)
	String timePersistenceValue;
	
	@JsonProperty(BlockAttributesMapping.TIMEPERSISTENCEUNIT)
	String timePersistenceUnit;

	@JsonProperty(BlockAttributesMapping.FIRSTOPERAND)
	OperandModel firstOperand;
	
	@JsonProperty(BlockAttributesMapping.SECONDOPERAND)
	OperandModel secondOperand;
	

	
	@JsonProperty(BlockAttributesMapping.OPERATIONID)
	String operatorId;



	public String getProjectId() {
		return projectId;
	}



	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}



	public String getRuleId() {
		return ruleId;
	}



	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}



	public String getBlockId() {
		return blockId;
	}



	public void setBlockId(String blockId) {
		this.blockId = blockId;
	}



	public String getBlockType() {
		return blockType;
	}



	public void setBlockType(String blockType) {
		this.blockType = blockType;
	}



	public String getTimePersistenceOn() {
		return timePersistenceOn;
	}



	public void setTimePersistenceOn(String timePersistenceOn) {
		this.timePersistenceOn = timePersistenceOn;
	}



	public String getTimePersistenceValue() {
		return timePersistenceValue;
	}



	public void setTimePersistenceValue(String timePersistenceValue) {
		this.timePersistenceValue = timePersistenceValue;
	}



	public String getTimePersistenceUnit() {
		return timePersistenceUnit;
	}



	public void setTimePersistenceUnit(String timePersistenceUnit) {
		this.timePersistenceUnit = timePersistenceUnit;
	}



	public OperandModel getFirstOperand() {
		return firstOperand;
	}



	public void setFirstOperand(OperandModel firstOperand) {
		this.firstOperand = firstOperand;
	}



	public OperandModel getSecondOperand() {
		return secondOperand;
	}



	public void setSecondOperand(OperandModel secondOperand) {
		this.secondOperand = secondOperand;
	}



	public String getOperatorId() {
		return operatorId;
	}



	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}



	@Override
	public String toString() {
		return "BlockModel [projectId=" + projectId + ", ruleId=" + ruleId + ", blockId=" + blockId + ", blockType="
				+ blockType + ", timePersistenceOn=" + timePersistenceOn + ", timePersistenceValue="
				+ timePersistenceValue + ", timePersistenceUnit=" + timePersistenceUnit + ", firstOperand="
				+ firstOperand + ", secondOperand=" + secondOperand + ", operatorId=" + operatorId + "]";
	}
	
	



}