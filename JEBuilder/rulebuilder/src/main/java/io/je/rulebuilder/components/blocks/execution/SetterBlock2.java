package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5004
 */
public class SetterBlock2 extends ExecutionBlock {
		
	String getterId;
	String attributeName;
	ValueType newValueType; //static , variable, attribute
	
	//static
	Object value;
	
	//variable
	String variableId;
	
	//DM
	String instanceId ; 
	
	
	

	public SetterBlock2(BlockModel blockModel) {
		super(blockModel);
		try
		{
		
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			instanceId = blockModel.getBlockConfiguration().getValue();
			isProperlyConfigured=true;
		}catch(Exception e) {
			isProperlyConfigured=false;
		
		}
		


	}

	public SetterBlock2() {
		super();
	}



	 
	@Override
	public String getExpression() throws RuleBuildFailedException {		
	   switch(newValueType)
	   {
	   case STATIC :
		   return "Executioner.writeToInstance("+instanceId+", "+attributeName +", "+value+");";
	   case VARIABLE:
		   return "Executioner.writeToInstance("+instanceId+", "+attributeName +", VariableManager.getVariable("+variableId+"));";
	   case ATTRIBUTE :
		   return "Executioner.writeToInstance("+instanceId+", "+attributeName +", InstanceManager.getVariable("+variableId+"));";
	  default:
		  throw new RuleBuildFailedException("INVALID CONFIGURATION");

	   }
	   
	  
	}



}
