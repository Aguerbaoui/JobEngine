package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class ExpBlock extends SingleInputArithmeticBlock {

	public ExpBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ExpBlock() {
		
	}
	

	
	

	@Override
	protected String getFormula() {
		return "JECalculator.exp( " +getInputRefName(0) + ")" ;
	}

	



}
