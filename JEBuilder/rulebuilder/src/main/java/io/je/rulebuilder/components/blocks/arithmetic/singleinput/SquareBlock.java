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
	protected String getFormula() {
		return "Math.pow( " +getInputRefName(0) + " , 2)" ;
	}

	



}
