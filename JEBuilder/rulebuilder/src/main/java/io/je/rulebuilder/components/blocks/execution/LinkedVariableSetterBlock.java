package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

/*
 * Block used to writing in a variable
 * source : previous block
 * operation id : 5007
 */
public class LinkedVariableSetterBlock extends ExecutionBlock {
	

	
	/*******************************Instance definition*******************************/
	String variableId ; 
	boolean  ignoreWriteIfSameValue=true;


	public LinkedVariableSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try {
			ignoreWriteIfSameValue=(boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
		}catch (Exception e) {
			// TODO: handle exception
		}
		try
		{
			variableId=(String) blockModel.getBlockConfiguration().get("variableId");

			isProperlyConfigured=true;
			if(inputBlockIds.isEmpty())
			{
				isProperlyConfigured=false;

			}
		}catch(Exception e) {
			isProperlyConfigured=false;
		}
		


	}

	public LinkedVariableSetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() {		
		  return "Executioner.updateVariableValue( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.ruleId  +"\","
				  +"\"" + this.variableId  +"\", "
				  + inputBlocks.get(0).getReference()
				  +", "+"\"" + blockName   +"\","
				  + this.ignoreWriteIfSameValue
				  +");\r\n";
			

	}



}
