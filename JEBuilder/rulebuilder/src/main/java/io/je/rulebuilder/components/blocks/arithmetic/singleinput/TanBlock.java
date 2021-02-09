package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class TanBlock extends SingleInputArithmeticBlock {

	public TanBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  TanBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.tan( $" +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.tan( $" +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.tan( $" +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
