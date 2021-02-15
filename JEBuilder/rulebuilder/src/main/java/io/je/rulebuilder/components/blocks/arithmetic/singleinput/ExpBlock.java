package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class ExpBlock extends SingleInputArithmeticBlock {

	public ExpBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ExpBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.exp( " +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.exp( " +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.exp( " +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
