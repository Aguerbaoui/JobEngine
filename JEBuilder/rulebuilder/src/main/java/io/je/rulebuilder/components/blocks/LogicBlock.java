package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class LogicBlock extends PersistableBlock {

	public LogicBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), 
				blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds(),blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());
	}



}
