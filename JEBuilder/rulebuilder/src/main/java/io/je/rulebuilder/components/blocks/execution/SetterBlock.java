package io.je.rulebuilder.components.blocks.execution;

import org.apache.commons.lang3.StringUtils;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
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
	String destinationClassId;

	
	//Constants
	String executionerMethod= "Executioner.writeToInstance(";
	

	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
		
			value = blockModel.getBlockConfiguration().get(AttributesMapping.NEWVALUE);
			newValueType = ValueType.valueOf((String)blockModel.getBlockConfiguration().get(AttributesMapping.TYPE));
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.DESTINATION_ATTRIBUTE_NAME);
			sourceAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
			sourceInstanceId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.OBJECTID);
			destinationInstanceId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE2);
			destinationClassId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.DESTINATION_CLASSID);
			variableId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.OBJECTID);
			
			
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
