package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class LowLimitingBlock extends MultipleInputArithmeticBlock {

	public LowLimitingBlock(BlockModel blockModel) {
		super(blockModel);
		// TODO Auto-generated constructor stub
	}
	
	private LowLimitingBlock()
	{
		
	}

	@Override
	protected String getArithmeticFormula(int level,String type) {
		return "MathUtilities.lowLimiting( "  ;

	}

}
