package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class SquareBlock extends SingleInputArithmeticBlock {

	public SquareBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  SquareBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.pow( " +getInputRefName(0) + " ,2)" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.pow( " +getInputRefName(0) + " , 2 )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.pow( " +getInputRefName(0) + " , 2)" ;
		
		}
	
	}

	
	


	



}
