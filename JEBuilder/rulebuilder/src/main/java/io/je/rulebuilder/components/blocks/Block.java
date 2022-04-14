package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import io.je.rulebuilder.components.CustomBlockLink;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
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
   
   @Transient
	HashMap<String,CustomBlockLink> customInputs = new HashMap<String, CustomBlockLink>() ;

	@Transient
	protected boolean includesOperation = false;
   
   protected boolean alreadyScripted=false;

  
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
	
	protected Block getInput(int i)
	{
		for(var entry : customInputs.entrySet())
		{
			if(entry.getValue().getOrder()==i)
				return entry.getValue().getBlock();
		}
		return null;
	}
	
	protected String getInputByName(int i)
	{
		for(var entry : customInputs.entrySet())
		{
			if(entry.getValue().getOrder()==i)
				return entry.getValue().getAttributeName();
		}
		return null;
	}
	
	public Block() {
		
	}

	public void addInput(Block block)
	{

			inputBlocks.add(block);
		
		
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
	
	public  abstract String getAsOperandExpression() throws RuleBuildFailedException;

	
	






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
	public String getRefName(String optional)
	{
		String var = ""; 
		
		if(this instanceof InstanceGetterBlock)
		{//get attribute var name
			var = (( InstanceGetterBlock )this).getAttributeVariableName(optional);
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
		if(inputBlocks.get(index) instanceof VariableGetterBlock)
		{
			var = (( VariableGetterBlock )inputBlocks.get(index)).getAttributeVariableName();
		}
		
		else 
		{//get block name as variable
			var =  inputBlocks.get(index).getBlockNameAsVariable();
		}
		return var;
	}
	
	//get name of input of index i
		//example : block A has 2 inputs Block B and Block C
		//blockA.getInputRefName(1) returns "$blockC";
		public String getInputRefName(int index,String attName)
		{
			String var = ""; 
			if(inputBlocks.get(index) instanceof VariableGetterBlock)
			{
				var = (( VariableGetterBlock )inputBlocks.get(index)).getAttributeVariableName();
			}else if(inputBlocks.get(index) instanceof InstanceGetterBlock)
			{
				var = (( InstanceGetterBlock )inputBlocks.get(index)).getAttributeVariableName(attName);

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

public boolean isAlreadyScripted() {
	return alreadyScripted;
}

public void setAlreadyScripted(boolean alreadyScripted) {
	this.alreadyScripted = alreadyScripted;
}

public String getPersistence() {
	if(this instanceof PersistableBlock )
	{
		PersistableBlock pBlock = (PersistableBlock) this;
		String persistence = pBlock.getPersistanceExpression();
		if(persistence!=null)
		{
			return persistence;
		}else if(pBlock.inputBlocks.isEmpty()){
			return null;
				
		}else {
			for (Block b : inputBlocks)
			{
				if(b.getPersistence()!=null)
				{
					return b.getPersistence();
				}
			}
		}
		
	}
	return null;
}

public HashMap<String, CustomBlockLink> getCustomInputs() {
	return customInputs;
}

public void setCustomInputs(HashMap<String, CustomBlockLink> customInputs) {
	this.customInputs = customInputs;
}

public void setIncludeOperation(boolean includeOperation) {
	if(this.inputBlocks.isEmpty())
	{
		this.includesOperation=includeOperation;
		return;
	}
	for(Block  b : this.inputBlocks)
	{
		this.includesOperation=includeOperation;
		b.setIncludeOperation(includeOperation);
	}
}





}
