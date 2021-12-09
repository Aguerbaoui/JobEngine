package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class HighLimitingBlock extends MultipleInputArithmeticBlock {

	String limit;

	public HighLimitingBlock(BlockModel blockModel) {
		super(blockModel);
		limit=String.valueOf( blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));

	}

	private HighLimitingBlock()
	{
		
	}

	@Override
	protected String getArithmeticFormula(int level,String type) {
		return "MathUtilities.highLimiting( "+limit+","  ;

	}

}
