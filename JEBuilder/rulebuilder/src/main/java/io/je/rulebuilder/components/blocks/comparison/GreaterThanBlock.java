package io.je.rulebuilder.components.blocks.comparison;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.models.BlockModel;

public class GreaterThanBlock extends ComparisonBlock {

	public GreaterThanBlock(BlockModel blockModel) {
		super(blockModel);
	}

	@Override
	public String getOperator() {
		return ">";
	}

	@Override
	public String getComparableExpression(String constraint) {
		// TODO Auto-generated method stub
		return null;
	}


}
