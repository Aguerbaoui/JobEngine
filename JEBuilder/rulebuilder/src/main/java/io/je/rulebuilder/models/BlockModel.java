package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;
import io.je.rulebuilder.config.AttributesMapping;

import java.util.ArrayList;


public class BlockModel {


    String projectId;

    String ruleId;

    @JsonProperty(AttributesMapping.BLOCKID)
    String blockId;

    @JsonProperty(AttributesMapping.TIMEPERSISTENCEVALUE)
    int timePersistenceValue;

    @JsonProperty(AttributesMapping.TIMEPERSISTENCEUNIT)
    TimePersistenceUnit timePersistenceUnit;

    @JsonProperty(AttributesMapping.OPERATIONID)
	int operationId;
    
    @JsonProperty(AttributesMapping.INPUTBLOCK)
	ArrayList<String> inputBlocksIds;
    
    @JsonProperty(AttributesMapping.OUTPUTBLOCK)
   	ArrayList<String> outputBlocksIds;
    
    @JsonProperty(AttributesMapping.BLOCKCONFIG)
	BlockConfigurationModel blockConfiguration;
    
    

	private BlockModel() {
		//TODO: throw exception if any of the ids is null
	}

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

	public int getTimePersistenceValue() {
		return timePersistenceValue;
	}

	public void setTimePersistenceValue(int timePersistenceValue) {
		this.timePersistenceValue = timePersistenceValue;
	}

	public TimePersistenceUnit getTimePersistenceUnit() {
		return timePersistenceUnit;
	}

	public void setTimePersistenceUnit(TimePersistenceUnit timePersistenceUnit) {
		this.timePersistenceUnit = timePersistenceUnit;
	}

	public int getOperationId() {
		return operationId;
	}

	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}
	
	

	public ArrayList<String> getOutputBlocksIds() {
		return outputBlocksIds;
	}

	public void setOutputBlocksIds(ArrayList<String> outputBlocksIds) {
		this.outputBlocksIds = outputBlocksIds;
	}

	public ArrayList<String> getInputBlocksIds() {
		return inputBlocksIds;
	}

	public void setInputBlocksIds(ArrayList<String> inputBlocksIds) {
		this.inputBlocksIds = inputBlocksIds;
	}

	public BlockConfigurationModel getBlockConfiguration() {
		return blockConfiguration;
	}

	public void setBlockConfiguration(BlockConfigurationModel blockConfiguration) {
		this.blockConfiguration = blockConfiguration;
	}




}