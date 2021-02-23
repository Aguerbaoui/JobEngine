package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.utilities.exceptions.RuleBuildFailedException;
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
	
	
	public String getBlockNameAsVariable()
	{
		return "$"+blockName ;
	}
	
	//return drl expression of block 
	public  abstract String getExpression() throws RuleBuildFailedException;

	//return drl expression of block as a first operand (used to optimise comparison blocks in order to avoid using eval)
	public  abstract String getAsFirstOperandExpression() throws RuleBuildFailedException;
	
	//get drl expression mapped to id (getter blocks) ex: Person($id == "123")
	public  abstract String getJoinExpression() throws RuleBuildFailedException;

	//get id variable name used in drl ex: $id
	public  String getJoinId()
	{
		if(!inputBlocks.isEmpty() && inputBlocks.get(0)!=null)
		{
			return inputBlocks.get(0).getJoinId();
		}
		return null;
	}
	
	//get a joined expression. example : Person(jobEngineElementID == $id )
	public  abstract String getJoinedExpression(String joinId) throws RuleBuildFailedException;
	
	public  abstract String getJoinedExpressionAsFirstOperand(String joinId) throws RuleBuildFailedException;
	
	public  abstract String getJoinExpressionAsFirstOperand() throws RuleBuildFailedException;


	public String getInputRefName(int index)
	{
		String var = ""; 
		if(inputBlocks.get(index) instanceof AttributeGetterBlock)
		{
			var = (( AttributeGetterBlock )inputBlocks.get(index)).getAttributeVariableName();
		}
		else 
		{
			var =  inputBlocks.get(index).getBlockNameAsVariable();
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

	//ignore block 
	protected void ignoreBlock()
	{
		for(Block inputBlock : inputBlocks)
		{
			inputBlock.outputBlocks.addAll(outputBlocks);
		}
		
		for(Block outputBlock : outputBlocks)
		{
			outputBlock.inputBlocks.addAll(inputBlocks);
		}
	}





}
