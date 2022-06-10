package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5008
 * source:input block
 * destination : Linked to getter
 */
public class LinkedAttachedSetterBlock extends ExecutionBlock {
		
	//SOURCE
	ValueType sourceType; //Static , Dynamic
	
	//static
	Object value;
	

	
	//DESTINATION
	String getterName;
	String destinationAttributeName;

	boolean  ignoreWriteIfSameValue=true;
	//Constants
	String executionerMethod= "Executioner.writeToInstance(";
	

	public LinkedAttachedSetterBlock(BlockModel blockModel) {
		super(blockModel);

		try {
			ignoreWriteIfSameValue=(boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
		}catch (Exception e) {
		}		
		try
		{
			value = blockModel.getBlockConfiguration().get(AttributesMapping.NEWVALUE);
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.DESTINATION_ATTRIBUTE_NAME);			
			getterName =  (String) blockModel.getBlockConfiguration().get(AttributesMapping.LINKED_GETTER_NAME);
			isProperlyConfigured=true;
			if(inputBlockIds.size()!=1)
			{
				isProperlyConfigured=false;

			}
		}catch(Exception e) {
			isProperlyConfigured=false;
		
		}
		


	}

	public LinkedAttachedSetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() {		
		StringBuilder expression = new StringBuilder();
		String getterInstanceId = getterName.replaceAll("\\s+", "")+ ".getJobEngineElementID()";
		expression.append(  "Executioner.updateInstanceAttributeValueFromStaticValue( "
				 +"\"" + this.jobEngineProjectID  +"\","
				  +"\"" + this.ruleId  +"\","
				  +"\"" + this.blockName  +"\","				  					  
				  + getterInstanceId  +","
				  +"\"" + this.destinationAttributeName  +"\","
				  + inputBlocks.get(0).getReference() +","
				  + this.ignoreWriteIfSameValue 
				  +");\r\n");
				expression.append("\n");
			
		
		
	   return expression.toString();

	}

}
