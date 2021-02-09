package io.je.rulebuilder.components.blocks.logic;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class JoinBlock extends LogicBlock {
	
	
	public JoinBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private JoinBlock() {
	}

	
	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		String joinId = inputBlocks.get(0).getJoinId();
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append("\n");
		expression.append("\n");
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append("\n");

		}

		return expression.toString();
	}










}
