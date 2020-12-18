package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.BlockInventory;
import io.je.rulebuilder.models.BlockModel;

public class SumBlock extends ArithmeticBlock {

	public SumBlock(BlockModel blockModel) {
		super(blockModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getExpression() {
		BlockInventory.addBlock(null);
		return null;
	}

}
