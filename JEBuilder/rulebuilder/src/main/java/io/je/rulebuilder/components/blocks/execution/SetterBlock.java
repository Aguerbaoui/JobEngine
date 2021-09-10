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
 * destination : Data Model Instance
 */
public class SetterBlock extends ExecutionBlock {
		
	//SOURCE
	ValueType sourceType; //ATTRIBUTE/STATIC/VARIBLE
	
	//static
	Object value;
	
	//variable
	String variableId;
	
	//DM
	String sourceClassName;
	String sourceInstanceId ; 
	String sourceAttributeName;
	
	//SOURCE
	ValueType destinationType; //ATTRIBUTE/VARIBLE
	
	//DESTINATION
	String destinationInstanceId ; 
	String destinationAttributeName;
	String destinationClassName;

	//variable
	String destinationVariableId;
	
	//Constants
	String executionerMethod= "Executioner.writeToInstance(";
	

	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
		//source configuration 
			
			//source type
			sourceType = ValueType.valueOf((String)blockModel.getBlockConfiguration().get("sourceValueType"));

			//if source data model
			sourceClassName =(String) blockModel.getBlockConfiguration().get("class_name");
			sourceAttributeName = (String) blockModel.getBlockConfiguration().get("attribute_name");
			sourceInstanceId = (String) blockModel.getBlockConfiguration().get("objectId");
			
			//if source variable
			variableId = (String) blockModel.getBlockConfiguration().get("objectId");

			value = blockModel.getBlockConfiguration().get("newValue");
		//destination configuration 

			destinationType = ValueType.valueOf((String)blockModel.getBlockConfiguration().get("destinationType"));

			
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get("destinationAttributeName");			
			destinationInstanceId = (String) blockModel.getBlockConfiguration().get("destinationInstanceId");
			destinationClassName = (String) blockModel.getBlockConfiguration().get("destinationClassName");
		
			destinationVariableId = (String) blockModel.getBlockConfiguration().get("destinationVariableId");

			
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
		
	  if(destinationType.equals(ValueType.ATTRIBUTE))
	  {
		  switch(sourceType)
		   {
		   case STATIC :		   
			   return "Executioner.updateInstanceAttributeValueFromStaticValue( "
				  +"\"" + this.destinationInstanceId  +"\","
				  +"\"" + this.destinationAttributeName  +"\","
				  +"\"" + this.value  +"\""
				  +");\r\n";
		   case VARIABLE:
			   return "Executioner.updateInstanceAttributeValueFromVariable( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.ruleId  +"\","
				  +"\"" + this.sourceInstanceId  +"\","
				  +"\"" + this.sourceAttributeName  +"\","
				  +"\"" + this.variableId  +"\""
				  +");\r\n";
		   case ATTRIBUTE :
			  return "Executioner.updateInstanceAttributeValueFromAnotherInstance( "
					  +"\"" + this.jobEngineProjectID  +"\","
					  +"\"" + this.ruleId  +"\","
					  +"\"" + this.sourceInstanceId  +"\","
					  +"\"" + this.sourceAttributeName  +"\","
					  +"\"" + this.destinationInstanceId  +"\","
					  +"\"" + this.destinationAttributeName  +"\""
					  +");\r\n";
			  		
		  default:
			  throw new RuleBuildFailedException("INVALID CONFIGURATION");

		   }
		   
	  }else if(destinationType.equals(ValueType.VARIABLE)) {
		  
		  switch(sourceType)
		   {
		   case STATIC :		   
			   return "Executioner.updateVariableValue( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.destinationVariableId  +"\","
				  +"\"" + this.value  +"\""
				  +");\r\n";
		   case VARIABLE:
			   return "Executioner.updateVariableValueFromAnotherVariable( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.variableId  +"\","
				  +"\"" + this.destinationVariableId  +"\""
				  +");\r\n";
		   case ATTRIBUTE :
			   return "Executioner.updateVariableValueFromDataModel( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.variableId  +"\","
				  +"\"" + this.sourceInstanceId  +"\","
				  +"\"" + this.sourceAttributeName  +"\""
				  +");\r\n";
			  		
		  default:
			  throw new RuleBuildFailedException("INVALID CONFIGURATION");

		   }
		  
	  }
	  return "";
	  
	}



}
