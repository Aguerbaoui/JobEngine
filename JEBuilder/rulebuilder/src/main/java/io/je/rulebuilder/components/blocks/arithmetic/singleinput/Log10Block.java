package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class Log10Block extends SingleInputArithmeticBlock {

	public Log10Block(BlockModel blockModel) {
		super(blockModel);
	}

	private  Log10Block() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.log10( $" +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.log10( $" +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.log10( $" +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
