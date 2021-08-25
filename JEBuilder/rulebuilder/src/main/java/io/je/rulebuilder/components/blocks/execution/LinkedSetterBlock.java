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
	String attributeName;
	List<String> instances ; 

	public LinkedSetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
			classId=(String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
			classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
			attributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
			instances = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
			isProperlyConfigured=true;
		}catch(Exception e) {
			isProperlyConfigured=false;
		}finally {
			if(classId==null || classPath==null || attributeName==null)
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
			expression.append("Executioner.writeToInstance("+instance+", "+attributeName +", "+getInputRefName(0)+");");
			expression.append("\n");
		}
		
	   return expression.toString();

	}



}
