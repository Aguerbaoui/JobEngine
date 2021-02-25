package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class FactorialBlock extends SingleInputArithmeticBlock {

	String value = null;
	public FactorialBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	public FactorialBlock() {
		
	}
	

	
	@Override
	protected String getFormula() {
		return "JECalculator.factorial( " + asDouble(getInputRefName(0))+ ")" ;
	}


}
