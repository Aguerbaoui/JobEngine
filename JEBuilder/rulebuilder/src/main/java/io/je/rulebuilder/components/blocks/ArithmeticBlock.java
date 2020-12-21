package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class ArithmeticBlock extends ConditionBlock {
	
	//parameters for arithmetic blocks that require parameters 
		String value;

	protected ArithmeticBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getOperationId(), blockModel.getInputBlocksIds());
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			//TODO: switch on operation id to configure parameters
			value = blockModel.getBlockConfiguration().getValue();
			
		}
	}
	


	



}
