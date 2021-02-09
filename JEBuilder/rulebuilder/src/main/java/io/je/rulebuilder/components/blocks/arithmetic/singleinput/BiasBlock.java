package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class BiasBlock extends SingleInputArithmeticBlock {

	String value = null;
	public BiasBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}
	
	public BiasBlock() {
	
	}

	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return "Number() from " + " $"+getInputRefName(0)+ " + " + value ;
		case 1:
			return "Number(doubleValue " + Keywords.toBeReplaced +") from " + " $"+getInputRefName(0) + " + " + value;
		case 2:
			return "";
		default: 
			return "Number() from " + " $"+getInputRefName(0)+ " + " + value  ;
		
		}
	
	}





}
