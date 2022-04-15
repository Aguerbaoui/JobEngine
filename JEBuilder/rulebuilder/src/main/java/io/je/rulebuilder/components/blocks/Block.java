package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import io.je.rulebuilder.components.BlockLink;
import io.je.rulebuilder.components.BlockLinkModel;
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
   protected List<BlockLink> inputBlocks = new ArrayList<>();
   
   @Transient
   protected List<BlockLink> outputBlocks = new ArrayList<>();
   

	@Transient
	protected boolean includesOperation = false;
   
   protected boolean alreadyScripted=false;

  
   /*
    * to be persisted in mongo
    */
   protected List<BlockLinkModel> inputBlockIds = new ArrayList<>();
   
   
   protected List<BlockLinkModel> outputBlockIds = new ArrayList<>();
   
   
   
   
	public Block(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
		String blockDescription,List<BlockLinkModel> inputBlockIds, List<BlockLinkModel> outputBlocksIds) {
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

	public abstract String getReference(String optional);
	
	
	public void addInput(Block block)
	{

			inputBlocks.add(new BlockLink(block));
		
		
	}
	
	public void addOutput(Block block)
	{

			outputBlocks.add(new BlockLink(block));
		
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



	public List<BlockLink> getInputBlocks() {
		return inputBlocks;
	}


	public void setInputBlocks(List<BlockLink> inputBlocks) {
		this.inputBlocks = inputBlocks;
	}


	public List<BlockLink> getOutputBlocks() {
		return outputBlocks;
	}


	public void setOutputBlocks(List<BlockLink> outputBlocks) {
		this.outputBlocks = outputBlocks;
	}


	public boolean isIncludesOperation() {
		return includesOperation;
	}


	public void setIncludesOperation(boolean includesOperation) {
		this.includesOperation = includesOperation;
	}


	public String getBlockDescription() {
		return blockDescription;
	}




	public void setBlockDescription(String blockDescription) {
		this.blockDescription = blockDescription;
	}

	public List<BlockLinkModel> getInputBlockIds() {
		return inputBlockIds;
	}

	public void setInputBlockIds(List<BlockLinkModel> inputBlockIds) {
		this.inputBlockIds = inputBlockIds;
	}

	public List<BlockLinkModel> getOutputBlockIds() {
		return outputBlockIds;
	}

	public void setOutputBlockIds(List<BlockLinkModel> outputBlockIds) {
		this.outputBlockIds = outputBlockIds;
	}

	//ignore block 
	public void ignoreBlock()
	{
		for(var inputBlock : inputBlocks)
		{
			inputBlock.getBlock().outputBlocks.addAll(outputBlocks);
		}
		
		for(var outputBlock : outputBlocks)
		{
			outputBlock.getBlock().inputBlocks.addAll(inputBlocks);
		}
	}


	
public boolean isProperlyConfigured() {
		return isProperlyConfigured;
	}

	public void setProperlyConfigured(boolean isProperlyConfigured) {
		this.isProperlyConfigured = isProperlyConfigured;
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
			for (var  b : inputBlocks)
			{
				if(b.getBlock().getPersistence()!=null)
				{
					return b.getBlock().getPersistence();
				}
			}
		}
		
	}
	return null;
}



public void setIncludeOperation(boolean includeOperation) {
	if(this.inputBlocks.isEmpty())
	{
		this.includesOperation=includeOperation;
		return;
	}
	for(var  b : this.inputBlocks)
	{
		this.includesOperation=includeOperation;
		b.getBlock().setIncludeOperation(includeOperation);
	}
}





}
