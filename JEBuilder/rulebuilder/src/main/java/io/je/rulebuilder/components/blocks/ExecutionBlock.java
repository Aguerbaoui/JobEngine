package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class ExecutionBlock extends Block {

	public ExecutionBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks=" + outputBlocks
				+ ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID
				+ ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
    

}
