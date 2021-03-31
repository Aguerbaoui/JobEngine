package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class SumBlock extends MultipleInputArithmeticBlock {

	public SumBlock(BlockModel blockModel) {
		super(blockModel);
	}
	
	private SumBlock() {
		
	}

	@Override
	protected String getArithmeticFormula(int level) {
		return "JECalculator.sum( "  ;

	}


}
