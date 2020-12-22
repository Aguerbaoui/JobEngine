package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class ArithmeticBlock extends ConditionBlock {

	public ArithmeticBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
	}

}
