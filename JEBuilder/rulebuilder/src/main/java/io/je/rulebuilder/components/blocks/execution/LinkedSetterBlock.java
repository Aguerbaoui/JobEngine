package io.je.rulebuilder.components.blocks.execution;

import java.util.List;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * Block used to writing in an instance's attribute (from DM) 
 * source : previous block
 * operation id : 5003
 */
public class LinkedSetterBlock extends ExecutionBlock {
	

	
	/*******************************Instance definition*******************************/
	String classId;
	String classPath;
	String destinationAttributeName;
	String destinationAttributeType;

	List<String> instances ; 

	public LinkedSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
			classId=(String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
			destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
			instances = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
			isProperlyConfigured=true;
			if(inputBlockIds.isEmpty())
			{
				isProperlyConfigured=false;

			}
		}catch(Exception e) {
			isProperlyConfigured=false;
		}finally {
			if(classId==null || classPath==null || destinationAttributeName==null || instances==null || instances.isEmpty())
			{
				isProperlyConfigured=false;

			}
		}
		


	}

	public LinkedSetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() {		
		StringBuilder expression = new StringBuilder();
		for(String instance : instances)
		{
			expression.append(  "Executioner.updateInstanceAttributeValueFromStaticValue( "
					  +"\"" + instance  +"\","
					  +"\"" + this.destinationAttributeName  +"\","
					  + getInputRefName(0)
					  +");\r\n");
			expression.append("\n");
		}
		
	   return expression.toString();

	}



}
