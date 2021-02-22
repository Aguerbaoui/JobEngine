package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class SinBlock extends SingleInputArithmeticBlock {

	public SinBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  SinBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.sin( " +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.sin( " +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.sin( " +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
