package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.models.BlockModel;

public class LessOrEqualBlock extends ComparisonBlock {

	public LessOrEqualBlock(BlockModel blockModel) {
		super(blockModel);
		
	}

	public LessOrEqualBlock()
	{
		super();
	}

	@Override
	public String getOperator() {
		
		return "<=";
	}

}
