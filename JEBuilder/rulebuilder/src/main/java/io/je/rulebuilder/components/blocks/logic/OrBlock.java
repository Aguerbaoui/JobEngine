package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public  class OrBlock extends LogicBlock {

	
	public OrBlock(BlockModel blockModel) {
		super(blockModel);	
		operator = " || ";
	}

	 public OrBlock() {
		
	}

	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();

		expression.append(" ( ");
		for(int i=0; i<inputBlocks.size();i++)
		{
			expression.append(" ( ");
			expression.append(inputBlocks.get(i).getExpression());
			expression.append(" ) ");
			if (i < inputBlocks.size() - 1) { //&& !inputBlocks.get(i).getExpression().isEmpty()) {
				expression.append(operator);
			}
		}
		expression.append(" ) ");


/*
		// not (not A and not B)
		expression.append(" not ( ");
		for(int i=0; i<inputBlocks.size();i++)
		{
			expression.append(" not ( ");
			expression.append(inputBlocks.get(i).getExpression());
			expression.append(" ) ");
			if (i < inputBlocks.size() - 1) {
				expression.append(" and ");
			}
		}
		expression.append(" ) ");
*/

		return expression.toString();
	}

}
