package io.je.rulebuilder.models;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.components.BlockLinkModel;
import io.je.rulebuilder.config.AttributesMapping;


public class BlockModel {


    String projectId;

    String ruleId;

    @JsonProperty(AttributesMapping.BLOCKID)
    String blockId;
    
    @JsonProperty(AttributesMapping.BLOCKNAME)
    String blockName;
    
    @JsonProperty(AttributesMapping.DESC)
    String description;

    @JsonProperty(AttributesMapping.TIMEPERSISTENCEVALUE)
    int timePersistenceValue;
    @JsonProperty(AttributesMapping.TIMEPERSISTENCEUNIT)
    String timePersistenceUnit;

    @JsonProperty(AttributesMapping.OPERATIONID)
	int operationId;
    
    @JsonProperty(AttributesMapping.INPUTBLOCK)
	ArrayList<BlockLinkModel> inputBlocksIds = new ArrayList<>();
    
    @JsonProperty(AttributesMapping.OUTPUTBLOCK)
   	ArrayList<BlockLinkModel> outputBlocksIds = new ArrayList<>();
    
   // @JsonProperty(AttributesMapping.BLOCKCONFIG)
	//BlockConfigurationModel blockConfiguration;
    
    @JsonProperty(AttributesMapping.BLOCKCONFIG)
   	HashMap<String,Object> blockConfiguration;
    

    
    

	private BlockModel() {
		//TODO: throw exception if any of the ids is null
	}

	
	
	public String getBlockName() {
		return blockName;
	}



	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
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

	public String getTimePersistenceUnit() {
		return timePersistenceUnit;
	}

	public void setTimePersistenceUnit(String timePersistenceUnit) {
		this.timePersistenceUnit = timePersistenceUnit;
	}

	public int getOperationId() {
		return operationId;
	}

	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}
	
	

	public ArrayList<BlockLinkModel> getOutputBlocksIds() {
		return outputBlocksIds;
	}

	public void setOutputBlocksIds(ArrayList<BlockLinkModel> outputBlocksIds) {
		this.outputBlocksIds = outputBlocksIds;
	}

	public ArrayList<BlockLinkModel> getInputBlocksIds() {
		return inputBlocksIds;
	}

	public void setInputBlocksIds(ArrayList<BlockLinkModel> inputBlocksIds) {
		this.inputBlocksIds = inputBlocksIds;
	}

	public HashMap<String,Object> getBlockConfiguration() {
		return blockConfiguration;
	}

	public void setBlockConfiguration(HashMap<String,Object> blockConfiguration) {
		this.blockConfiguration = blockConfiguration;
	}






	@Override
	public String toString() {
		return "BlockModel [projectId=" + projectId + ", ruleId=" + ruleId + ", blockId=" + blockId + ", blockName="
				+ blockName + ", description=" + description + ", timePersistenceValue=" + timePersistenceValue
				+ ", timePersistenceUnit=" + timePersistenceUnit + ", operationId=" + operationId + ", inputBlocksIds="
				+ inputBlocksIds + ", outputBlocksIds=" + outputBlocksIds + ", blockConfiguration=" + blockConfiguration
				+ "]";
	}




}