package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
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
	protected String getArithmeticFormula(int level) { 
		switch(level)
		{
		case 0:
			return "Number() from JECalculator.changeSign( " + asDouble(getInputRefName(0))+ ")";
		case 1:
			return "Number(doubleValue " + Keywords.toBeReplaced +") " + "from JECalculator.changeSign( " + asDouble(getInputRefName(0))+ " ) ";
		case 2:
			return "";
		default: 
			return "Number() from JECalculator.changeSign( " + asDouble(getInputRefName(0))+ ")";
		
		}
	
	}


}
