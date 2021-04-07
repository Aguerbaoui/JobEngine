package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

public class SetterBlock extends ExecutionBlock {
	
	String type = null;
	String attributeNewValue;
	String attributeName;
	String joinId ; 
	
	public SetterBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			attributeNewValue = blockModel.getBlockConfiguration().getValue();
			type = blockModel.getBlockConfiguration().getType();
			attributeName = blockModel.getBlockConfiguration().getAttributeName();
			joinId = blockModel.getBlockConfiguration().getInstanceId();

		}

	}
	
	 public SetterBlock() {
		 super();
	}


	 
	@Override
	public String getExpression() {
		String instanceIdentifier = null;
		if(type.equalsIgnoreCase("instance"))
		{
			instanceIdentifier=joinId;
		}else if(type.equalsIgnoreCase("block"))
		{
			instanceIdentifier=   joinId.replaceAll("\\s+", "") + ".getJobEngineElementID()" ; 
		}
		
	   return "Executioner.writeToInstance("+instanceIdentifier+", "+attributeName +", "+attributeNewValue+");";

	}



}
