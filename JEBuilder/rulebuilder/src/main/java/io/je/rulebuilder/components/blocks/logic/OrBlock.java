package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public  class OrBlock extends LogicBlock {

	
	public OrBlock(BlockModel blockModel) {
		super(blockModel);
		operator = " or ";
	}

	 public OrBlock() {
		
	}

	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();

		// FIXME currently fires executions as much as true conditions => fire once
		for(int i=0; i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getExpression());
			if (i < inputBlocks.size() - 1) { //&& !inputBlocks.get(i).getExpression().isEmpty()) {
				expression.append(" or "); // FIXME operator / Or operation
			}
		}

		return expression.toString();
	}

}
