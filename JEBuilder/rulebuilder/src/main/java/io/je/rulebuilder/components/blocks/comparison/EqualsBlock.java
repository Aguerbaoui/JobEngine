package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.models.BlockModel;

public class EqualsBlock extends ComparisonBlock {

	public EqualsBlock(BlockModel blockModel) {
		super(blockModel);
		
	}

	public EqualsBlock()
	{
		super();
	}

	@Override
	//TODO: get operator based on operands type ( ex: equals if string etc )
	public String getOperator() {
		
		return "==";
	}

}
