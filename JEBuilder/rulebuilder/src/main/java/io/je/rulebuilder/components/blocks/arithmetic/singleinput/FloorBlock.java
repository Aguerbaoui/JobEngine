package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class FloorBlock extends SingleInputArithmeticBlock {

	public FloorBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  FloorBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.floor(" +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.floor(" +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.floor(" +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
