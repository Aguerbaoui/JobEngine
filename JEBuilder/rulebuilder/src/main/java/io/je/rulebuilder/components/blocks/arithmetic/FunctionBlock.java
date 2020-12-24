package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.models.BlockModel;

public class FunctionBlock extends ArithmeticBlock {

	int operationId;
	public FunctionBlock(BlockModel blockModel,int operationId) {
		super(blockModel);
		this.operationId=operationId;
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

}
