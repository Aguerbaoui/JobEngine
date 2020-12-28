package io.je.rulebuilder.components.blocks.logic;

import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;

public class JoinBlock extends LogicBlock {
	
	String joinId;

	public JoinBlock(BlockModel blockModel) {
		super(blockModel);
		joinId="joinId";
	}

	
	
	@Override
	public String getComparableExpression(String constraint) {
		return null;
	}



}
