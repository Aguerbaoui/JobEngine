package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class GetterBlock extends ConditionBlock  {

		

	public GetterBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getOperationId(),
				blockModel.getInputBlocksIds());

	}


	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}
}
