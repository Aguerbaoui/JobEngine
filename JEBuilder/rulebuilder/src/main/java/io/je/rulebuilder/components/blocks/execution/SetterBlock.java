package io.je.rulebuilder.components.blocks.execution;

import org.apache.commons.lang3.StringUtils;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5005
 * source:DM/Variable
 * destination : Linked to getter
 */
public class SetterBlock extends ExecutionBlock {
		
	//SOURCE
	ValueType newValueType; //Static , Dynamic
	
	//static
	Object value;
	
	//variable
	String variableId;
	
	//DM
	String sourceInstanceId ; 
	String sourceAttributeName;
	
	//DESTINATION
	String destinationInstanceId ; 
	String destinationAttributeName;

	
	//Constants
	String executionerMethod= "Executioner.writeToInstance(";
	

	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
		
			value = blockModel.getBlockConfiguration().getNewValue();
			newValueType = ValueType.valueOf(blockModel.getBlockConfiguration().getType());
			destinationAttributeName = blockModel.getBlockConfiguration().getDestinationAttributeName();
			sourceAttributeName = blockModel.getBlockConfiguration().getAttributeName();
			sourceInstanceId = blockModel.getBlockConfiguration().getValue();
			destinationInstanceId = blockModel.getBlockConfiguration().getValue2();

			variableId = blockModel.getBlockConfiguration().getValue();
			
			
			isProperlyConfigured=true;
		}catch(Exception e) {
			isProperlyConfigured=false;
		
		}
		


	}

	public SetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() throws RuleBuildFailedException {		
		
	   switch(newValueType)
	   {
	   case STATIC :		   
		   return executionerMethod+destinationInstanceId+", "+destinationAttributeName +", "+value+");";
	   case VARIABLE:
		   return executionerMethod+destinationInstanceId+", "+destinationAttributeName +", VariableManager.getVariable("+variableId+"));";
	   case ATTRIBUTE :
		   return executionerMethod+destinationInstanceId+", "+destinationAttributeName +", InstanceManager.getInstance("+sourceInstanceId+").get"+ StringUtils.capitalize(sourceAttributeName)+ "());";
	  default:
		  throw new RuleBuildFailedException("INVALID CONFIGURATION");

	   }
	   
	  
	}



}
