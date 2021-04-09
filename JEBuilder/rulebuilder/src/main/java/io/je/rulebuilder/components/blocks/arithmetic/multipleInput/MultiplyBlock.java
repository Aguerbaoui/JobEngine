package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class MultiplyBlock extends MultipleInputArithmeticBlock {

	public MultiplyBlock(BlockModel blockModel) {
		super(blockModel);
	}
	
	private MultiplyBlock() {
		
	}

	@Override
	protected String getArithmeticFormula(int level,String type) {
		return "JECalculator.multiply( "  ;

	}


}
