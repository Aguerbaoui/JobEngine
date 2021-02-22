package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class ArcsinBlock extends SingleInputArithmeticBlock {

	public ArcsinBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ArcsinBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "Math.asin( " +getInputRefName(0) + ")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.asin( " +getInputRefName(0) + " )" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "Math.asin( " +getInputRefName(0) + ")" ;
		
		}
	
	}

	
	


	



}
