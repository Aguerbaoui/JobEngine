package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public abstract class ArithmeticBlock extends ConditionBlock {

	public ArithmeticBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription());

	}

	public ArithmeticBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ArithmeticBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";

	}

	
	public String asDouble(String val)
	{
		return " Double.valueOf( "+val+" )";
	}
	
	protected abstract String getArithmeticFormula(int level,String type);
	

	
	

}
