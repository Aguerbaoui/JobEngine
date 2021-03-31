package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class HighLimitingBlock extends MultipleInputArithmeticBlock {

	public HighLimitingBlock(BlockModel blockModel) {
		super(blockModel);
		// TODO Auto-generated constructor stub
	}
	
	private HighLimitingBlock()
	{
		
	}

	@Override
	protected String getArithmeticFormula(int level) {
		return "JECalculator.highLimiting( "  ;

	}

}