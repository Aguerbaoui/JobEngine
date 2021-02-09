package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class AbsBlock extends SingleInputArithmeticBlock {

	public AbsBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  AbsBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.abs( $" +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.abs( $" +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.abs( $" +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
