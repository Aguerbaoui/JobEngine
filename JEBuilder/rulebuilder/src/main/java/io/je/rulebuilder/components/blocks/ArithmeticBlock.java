package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class ArithmeticBlock extends ConditionBlock {

	public ArithmeticBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription(),blockModel.getInputBlocksIds(),blockModel.getOutputBlocksIds());

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
	
	
	/*
	 * returns the block expression
	 * level : to test if it's a comparable expression or not
	 * type :  type of the expected result 
	 */
	protected abstract String getArithmeticFormula(int level,String type);
	

	
	

}
