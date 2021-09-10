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

	public LinkedVariableSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
			variableId=(String) blockModel.getBlockConfiguration().get("variableId");
			
			isProperlyConfigured=true;
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
				  +"\"" + this.variableId  +"\", "
				  + getInputRefName(0)
				  +");\r\n";
			

	}



}
