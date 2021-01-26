package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.models.BlockModel;

public class GreaterOrEqualBlock extends ComparisonBlock {

	public GreaterOrEqualBlock(BlockModel blockModel) {
		super(blockModel);
		
	}

	public GreaterOrEqualBlock()
	{
		super();
	}

	@Override
	public String getOperator() {
		
		return ">=";
	}

}
