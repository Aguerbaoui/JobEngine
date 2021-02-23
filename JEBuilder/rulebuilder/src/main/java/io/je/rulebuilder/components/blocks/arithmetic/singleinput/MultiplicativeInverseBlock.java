package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class MultiplicativeInverseBlock extends SingleInputArithmeticBlock {

	String value = null;
	public MultiplicativeInverseBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	public MultiplicativeInverseBlock() {
		
	}
	


	@Override
	protected String getFormula() {
		return "JECalculator.multiplicativeInverse( " + asDouble(getInputRefName(0))+ ")" ;
	}

}
