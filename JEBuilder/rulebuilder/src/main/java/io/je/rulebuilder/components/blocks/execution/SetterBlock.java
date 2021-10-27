package io.je.rulebuilder.components.blocks.execution;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.constants.JEMessages;
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
	String sourceVariableId;
	
	//DM
	String sourceClassName;
	String sourceInstanceId ; 
	String sourceAttributeName;
	
	//SOURCE
	ValueType destinationType; //ATTRIBUTE/VARIBLE
	
	//DESTINATION
	List<String> destinationInstancesId ; 
	String destinationAttributeName;
	String destinationAttributeType;
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
			sourceInstanceId = (String) blockModel.getBlockConfiguration().get("sourceInstance");
			
			//if source variable
			sourceVariableId = (String) blockModel.getBlockConfiguration().get("sourceVariable");

			value = blockModel.getBlockConfiguration().get("newValue");
		//destination configuration 

			destinationType = ValueType.valueOf((String)blockModel.getBlockConfiguration().get("destinationType"));

			
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get("destinationAttributeName");			
			
			if( blockModel.getBlockConfiguration().containsKey(AttributesMapping.SPECIFICINSTANCES) )
			{
				destinationInstancesId = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
			}
			
			destinationClassName = (String) blockModel.getBlockConfiguration().get("destinationClassName");
			destinationAttributeType = (String) blockModel.getBlockConfiguration().get("destinationAttributeType");		
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

		   StringBuilder expression ;

	  if(destinationType.equals(ValueType.ATTRIBUTE))
	  {
		  switch(sourceType)
		   {
		   case STATIC :	
			   expression = new StringBuilder();
				for(String instanceId : destinationInstancesId)
				{
					expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
						   	  +"\"" + this.jobEngineProjectID  +"\","
							  +"\"" + this.ruleId  +"\","
							  +"\"" + this.blockName  +"\","				  
							  +"\"" + instanceId  +"\","
							  +"\"" + this.destinationAttributeName  +"\","
							  +"\"" + this.value  +"\""
							  +");\r\n");
					expression.append("\n");
				}
				return expression.toString();
		
		   case VARIABLE:
			   return "Executioner.updateInstanceAttributeValueFromVariable( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.ruleId  +"\","
				  +"\"" + this.sourceInstanceId  +"\","
				  +"\"" + this.sourceAttributeName  +"\","
				  +"\"" + this.sourceVariableId  +"\""
				  +");\r\n";
		   case ATTRIBUTE :
			    expression = new StringBuilder();
				for(String instanceId : destinationInstancesId)
				{
					expression.append("Executioner.updateInstanceAttributeValueFromAnotherInstance( "
							  +"\"" + this.jobEngineProjectID  +"\","
							  +"\"" + this.ruleId  +"\","
							  +"\"" + this.sourceInstanceId  +"\","
							  +"\"" + this.sourceAttributeName  +"\","
							  +"\"" + instanceId  +"\","
							  +"\"" + this.destinationAttributeName  +"\""
							  +");\r\n");
					expression.append("\n");
				}
				return expression.toString();
			  
			  		
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
				  +"\"" + this.sourceVariableId  +"\","
				  +"\"" + this.destinationVariableId  +"\""
				  +");\r\n";
		   case ATTRIBUTE :
			   return "Executioner.updateVariableValueFromDataModel( " 
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.destinationVariableId  +"\","
				  +"\"" + this.sourceInstanceId  +"\","
				  +"\"" + this.sourceAttributeName  +"\""
				  +");\r\n";
			  		
		  default:
			  throw new RuleBuildFailedException(JEMessages.INVALID_CONFIG);

		   }
		  
	  }
	  return "";
	  
	}



}
