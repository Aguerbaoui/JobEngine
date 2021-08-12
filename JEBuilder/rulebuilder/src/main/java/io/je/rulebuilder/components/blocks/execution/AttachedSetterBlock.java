package io.je.rulebuilder.components.blocks.execution;

import org.apache.commons.lang3.StringUtils;

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
	String sourceAttributeName;
	
	//DESTINATION
	String getterName;
	String destinationAttributeName;

	
	//Constants
	String executionerMethod= "Executioner.writeToInstance(";
	

	public AttachedSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
		
			value = blockModel.getBlockConfiguration().getNewValue();
			newValueType = ValueType.valueOf(blockModel.getBlockConfiguration().getType());
			destinationAttributeName = blockModel.getBlockConfiguration().getDestinationAttributeName();
			sourceAttributeName = blockModel.getBlockConfiguration().getAttributeName();
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
		   return executionerMethod+getterInstanceId+", "+destinationAttributeName +", "+value+");";
	   case VARIABLE:
		   return executionerMethod+getterInstanceId+", "+destinationAttributeName +", VariableManager.getVariable("+variableId+"));";
	   case ATTRIBUTE :
		   return executionerMethod+getterInstanceId+", "+destinationAttributeName +", InstanceManager.getInstance("+instanceId+").get"+ StringUtils.capitalize(sourceAttributeName)+ "());";
	  default:
		  throw new RuleBuildFailedException("INVALID CONFIGURATION");

	   }
	   
	  
	}



}
