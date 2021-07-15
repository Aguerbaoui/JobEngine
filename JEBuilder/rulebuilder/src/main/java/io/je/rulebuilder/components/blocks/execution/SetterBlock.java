package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

/*
 * Block used to writing in an instance's attribute (from DM) 
 * source : previous block
 * operation id : 5003
 */
public class SetterBlock extends ExecutionBlock {
	

	
	/*******************************Instance definition*******************************/
	String classId;
	String classPath;
	String attributeName;
	String instanceId ; 

	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		try
		{
			classId=blockModel.getBlockConfiguration().getClassId();
			classPath = blockModel.getBlockConfiguration().getClassName();
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			instanceId = blockModel.getBlockConfiguration().getValue();
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

	public SetterBlock() {
		super();
	}



	 
	@Override
	public String getExpression() {		
	   return "Executioner.writeToInstance("+instanceId+", "+attributeName +", "+getInputRefName(0)+");";

	}



}
