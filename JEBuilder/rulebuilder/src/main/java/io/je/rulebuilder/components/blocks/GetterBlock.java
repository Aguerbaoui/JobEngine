package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class GetterBlock extends ConditionBlock {

	public GetterBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription());
	}

	public GetterBlock() {
		super();
	}

	@Override
	public String toString() {
		return "GetterBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID="
				+ jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}

}
