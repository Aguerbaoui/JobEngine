package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class ChangeSignBlock extends SingleInputArithmeticBlock {

	String value = null;
	public ChangeSignBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	public ChangeSignBlock() {
		
	}
	

	@Override
	protected String getFormula() {
		return "JECalculator.changeSign( " + asDouble(getInputRefName(0))+ ")" ;
	}


}
