package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.utilities.runtimeobject.JEObject;

/*
 * Job Engine block
 */
@Document(collection="RuleBlock")
public abstract class Block extends JEObject {

  	
   protected String ruleId;    
   protected String blockName;
   protected String blockDescription;
   
   @Transient
   protected List<Block> inputBlocks = new ArrayList<>();
   
   @Transient
   protected List<Block> outputBlocks = new ArrayList<>();

  
   /*
    * to be persisted in mongo
    */
   protected List<String> inputBlockIds = new ArrayList<>();
   
   
   protected List<String> outputBlockIds = new ArrayList<>();
   
   
	public Block(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
		String blockDescription) {
	super(jobEngineElementID, jobEngineProjectID);
	this.ruleId = ruleId;
	this.blockName = blockName;
	this.blockDescription = blockDescription;
	
}
	
	public Block() {
		
	}

	public void addInput(Block block)
	{
		if(!inputBlocks.contains(block))
		{
			inputBlocks.add(block);
		}
		
	}
	
	public void addOutput(Block block)
	{
		if(!outputBlocks.contains(block))
		{
			outputBlocks.add(block);
		}
	}
	
	public  abstract String getExpression();

	public  abstract String getAsFirstOperandExpression();
	
	public  abstract String getAsSecondOperandExpression();

	
	public  abstract String getJoinedExpression();

	public String getInputRefName(int index)
	{
		String var = "";
		if(inputBlocks.get(index) instanceof AttributeGetterBlock)
		{
			var = (( AttributeGetterBlock )inputBlocks.get(index)).getAttributeName().replace(".", "");
		}
		else 
		{
			var = inputBlocks.get(index).getBlockName().replaceAll("\\s+","");
		}
		return var;
	}
	
	//getters and setters

	
	

	public String getRuleId() {
		return ruleId;
	}




	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}




	public String getBlockName() {
		return blockName;
	}




	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}




	public List<Block> getOutputBlocks() {
		return outputBlocks;
	}




	public void setOutputBlocks(List<Block> outputBlocks) {
		this.outputBlocks = outputBlocks;
	}




	public List<Block> getInputBlocks() {
		return inputBlocks;
	}




	public void setInputBlocks(List<Block> inputBlocks) {
		this.inputBlocks = inputBlocks;
	}




	public String getBlockDescription() {
		return blockDescription;
	}




	public void setBlockDescription(String blockDescription) {
		this.blockDescription = blockDescription;
	}

	public List<String> getInputBlockIds() {
		return inputBlockIds;
	}

	public void setInputBlockIds(List<String> inputBlockIds) {
		this.inputBlockIds = inputBlockIds;
	}

	public List<String> getOutputBlockIds() {
		return outputBlockIds;
	}

	public void setOutputBlockIds(List<String> outputBlockIds) {
		this.outputBlockIds = outputBlockIds;
	}





}
