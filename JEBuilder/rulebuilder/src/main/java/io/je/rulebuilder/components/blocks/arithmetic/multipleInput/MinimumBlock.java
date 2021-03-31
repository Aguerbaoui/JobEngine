package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class MinimumBlock extends MultipleInputArithmeticBlock {

	public MinimumBlock(BlockModel blockModel) {
		super(blockModel);
		// TODO Auto-generated constructor stub
	}
	
	private MinimumBlock()
	{
		
	}

	@Override
	protected String getArithmeticFormula(int level) {
		return "JECalculator.minimum( "  ;

	}

}
