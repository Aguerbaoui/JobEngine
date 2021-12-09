package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class LowLimitingBlock extends MultipleInputArithmeticBlock {

	String limit;
	public LowLimitingBlock(BlockModel blockModel) {
		super(blockModel);
		limit=String.valueOf( blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));

	}
	
	private LowLimitingBlock()
	{
		
	}

	@Override
	protected String getArithmeticFormula(int level,String type) {
		return "MathUtilities.lowLimiting( "+limit+","  ;

	}

}
