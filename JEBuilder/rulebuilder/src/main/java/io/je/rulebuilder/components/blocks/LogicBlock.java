package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public  class LogicBlock extends PersistableBlock {


	public LogicBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getOperationId(), blockModel.getInputBlocksIds(),
				blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());
		

}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}
}
