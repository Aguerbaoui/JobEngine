package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5004
 * source:DM/Variable
 * destination : Linked to getter
 */
public class AttachedSetterBlock extends ExecutionBlock {
		
	//SOURCE
	ValueType newValueType; //Static , Dynamic
	
	//static
	Object value;
	
	//variable
	String variableId;
	
	//DM
	String instanceId ; 
	
	//DESTINATION
	String getterName;
	String attributeName;

	

	public AttachedSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
		
			value = blockModel.getBlockConfiguration().getNewValue();
			newValueType = ValueType.valueOf(blockModel.getBlockConfiguration().getType());
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			instanceId = blockModel.getBlockConfiguration().getValue();
			variableId = blockModel.getBlockConfiguration().getValue();
			getterName =  blockModel.getBlockConfiguration().getLinkedGetterName();
			
			
			isProperlyConfigured=true;
		}catch(Exception e) {
			isProperlyConfigured=false;
		
		}
		


	}

	public AttachedSetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() throws RuleBuildFailedException {		
		
		String getterInstanceId = getterName.replaceAll("\\s+", "")+ ".getJobEngineElementID()";
	   switch(newValueType)
	   {
	   case STATIC :
		   
		   return "Executioner.writeToInstance("+getterInstanceId+", "+attributeName +", "+value+");";
	   case VARIABLE:
		   return "Executioner.writeToInstance("+getterInstanceId+", "+attributeName +", VariableManager.getVariable("+variableId+"));";
	   case ATTRIBUTE :
		   return "Executioner.writeToInstance("+getterInstanceId+", "+attributeName +", InstanceManager.getInstance("+instanceId+"));";
	  default:
		  throw new RuleBuildFailedException("INVALID CONFIGURATION");

	   }
	   
	  
	}



}
