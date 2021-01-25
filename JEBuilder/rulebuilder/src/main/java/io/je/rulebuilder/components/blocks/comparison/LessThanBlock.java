package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.models.BlockModel;

public class LessThanBlock extends ComparisonBlock {

	public LessThanBlock(BlockModel blockModel) {
		super(blockModel);
		
	}

	public LessThanBlock()
	{
		super();
	}

	@Override
	public String getOperator() {
		
		return "<";
	}

}
