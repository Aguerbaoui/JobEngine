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
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return "Number() from JECalculator.factorial( " + asDouble(" $"+getInputRefName(0))+ ")";
		case 1:
			return "Number(doubleValue " + Keywords.toBeReplaced +") " + "from JECalculator.factorial( " + asDouble(" $"+getInputRefName(0))+ " ) ";
		case 2:
			return "";
		default: 
			return "Number() from JECalculator.factorial( " + asDouble(" $"+getInputRefName(0))+ ")";
		
		}
	
	}


}
