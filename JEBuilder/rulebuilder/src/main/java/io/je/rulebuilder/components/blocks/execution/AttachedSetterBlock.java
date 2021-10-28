package io.je.rulebuilder.components.blocks.execution;

import org.apache.commons.lang3.StringUtils;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5004
 * source:DM/Variable
 * destination : Linked to getter
 */
public class AttachedSetterBlock extends ExecutionBlock {
		
	//SOURCE
	ValueType sourceType; //Static , Dynamic
	
	//static
	Object value;
	
	//variable
	String sourceVariableId;
	
	//DM
	String sourceInstanceId ; 
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
		
			value = blockModel.getBlockConfiguration().get(AttributesMapping.NEWVALUE);
			sourceType = ValueType.valueOf((String)blockModel.getBlockConfiguration().get(AttributesMapping.SOURCE_VALUE_TYPE));
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.DESTINATION_ATTRIBUTE_NAME);
			sourceAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
			sourceInstanceId = (String) blockModel.getBlockConfiguration().get("sourceInstance");
			sourceVariableId = (String) blockModel.getBlockConfiguration().get("sourceVariable");
			getterName =  (String) blockModel.getBlockConfiguration().get(AttributesMapping.LINKED_GETTER_NAME);
			
			
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
		 StringBuilder expression ;  
		switch(sourceType)
		   {
		   case STATIC :	
			    expression = new StringBuilder();
			 expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
						   	  +"\"" + this.jobEngineProjectID  +"\","
							  +"\"" + this.ruleId  +"\","
							  +"\"" + this.blockName  +"\","				  
							  + getterInstanceId  +","
							  +"\"" + this.destinationAttributeName  +"\","
							  +"\"" + this.value  +"\""
							  +");\r\n");
					expression.append("\n");
			
				return expression.toString();
		
		   case VARIABLE:
			   return "Executioner.updateInstanceAttributeValueFromVariable( "
				  +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.ruleId  +"\","
				  + getterInstanceId  +","
				  +"\"" + this.destinationAttributeName  +"\","
				  +"\"" + this.sourceVariableId  +"\""
				  +");\r\n";
		   case ATTRIBUTE :
			    expression = new StringBuilder();
				
					expression.append("Executioner.updateInstanceAttributeValueFromAnotherInstance( "
							  +"\"" + this.jobEngineProjectID  +"\","
							  +"\"" + this.ruleId  +"\","
							  +"\"" + this.sourceInstanceId  +"\","
							  +"\"" + this.sourceAttributeName  +"\","
							  + getterInstanceId  +","
							  +"\"" + this.destinationAttributeName  +"\""
							  +");\r\n");
					expression.append("\n");
				
				return expression.toString();
			  
			  		
		  default:
			  throw new RuleBuildFailedException(JEMessages.INVALID_CONFIG);

		   }

	  
	}



}
