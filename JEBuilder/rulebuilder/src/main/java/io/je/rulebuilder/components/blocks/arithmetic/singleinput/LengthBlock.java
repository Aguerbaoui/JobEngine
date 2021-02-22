package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class LengthBlock extends SingleInputArithmeticBlock {

	public LengthBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  LengthBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from "  +getInputRefName(0)+".length()"  ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " +getInputRefName(0)+".length()"  ;
		case 2:
			return "";
		default: 
			return " Number() from "  +getInputRefName(0)+".length()"  ;
		
		}
	
	}

	
	


	



}
