package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.models.BlockModel;

public class GreaterThanBlock extends ComparisonBlock {

	public GreaterThanBlock(BlockModel blockModel) {
		super(blockModel);
		
	}

	public GreaterThanBlock()
	{
		super();
	}

	@Override
	public String getOperator() {
		
		return ">";
	}

}
