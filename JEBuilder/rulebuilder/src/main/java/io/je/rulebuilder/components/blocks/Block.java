package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.components.blocks.getter.VariableGetterBlock;
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
   protected boolean isProperlyConfigured=true;
   
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
		String blockDescription,List<String> inputBlockIds, List<String> outputBlocksIds) {
	super(jobEngineElementID, jobEngineProjectID, blockName);
	this.ruleId = ruleId;
	this.blockName = blockName;
	this.blockDescription = blockDescription;
	isProperlyConfigured=true;
	if(blockName==null)
	{
		isProperlyConfigured=false;
	}
	this.inputBlockIds= inputBlockIds;
	this.outputBlockIds = outputBlocksIds;
	
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
	
	
	
	//return drl expression of block 
	public  abstract String getExpression() throws RuleBuildFailedException;

	//return drl expression of block as a first operand (used to optimise comparison blocks in order to avoid using eval)
	public  abstract String getAsOperandExpression() throws RuleBuildFailedException;
	
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



	/*
	 * returns "blockName" (remove spaces : TODO: check with Haroun about removing spaces)
	 */
	public String getBlockNameAsVariable()
	{
		return blockName.replaceAll("\\s+", "") ;
	}
	

	
	/*
	 * get name of variable holding he value expressed by input number index: ex: $age, $block1 ...
	 */
	public String getRefName()
	{
		String var = ""; 
		if(this instanceof AttributeGetterBlock)
		{//get attribute var name
			var = (( AttributeGetterBlock )this).getAttributeVariableName();
		}
		else 
		{//get block name as variable
			var =  this.getBlockNameAsVariable();
		}
		return var;
	}
	
	
	
	//get name of input of index i
	//example : block A has 2 inputs Block B and Block C
	//blockA.getInputRefName(1) returns "$blockC";
	public String getInputRefName(int index)
	{
		String var = ""; 
		if(inputBlocks.get(index) instanceof AttributeGetterBlock)
		{//get attribute var name
			var = (( AttributeGetterBlock )inputBlocks.get(index)).getAttributeVariableName();
		}else if(inputBlocks.get(index) instanceof VariableGetterBlock)
		{
			var = (( VariableGetterBlock )inputBlocks.get(index)).getAttributeVariableName();
		}
		else 
		{//get block name as variable
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
	public void ignoreBlock()
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


	
public boolean isProperlyConfigured() {
		return isProperlyConfigured;
	}

	public void setProperlyConfigured(boolean isProperlyConfigured) {
		this.isProperlyConfigured = isProperlyConfigured;
	}

public Block getInputById(String id)
{
	for(Block inputBlock : inputBlocks)
	{
		if (inputBlock.jobEngineElementID.equals(id))
		{
			return inputBlock;
		}
	}
	return null;
}

public  void addSpecificInstance(String instanceId) {
	
}



public void removeSpecificInstance() {
	// TODO Auto-generated method stub
	
}

}
