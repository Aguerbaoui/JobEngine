package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class GetterBlock extends ConditionBlock  {

	public GetterBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
	}

	@Override
	public String toString() {
		return "GetterBlock [ruleId=" + ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks=" + outputBlocks
				+ ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID
				+ ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	public  abstract String getComparableExpression(String constraint);
	
	


}
