package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {
	
	public SingleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);

	}
	

	protected SingleInputArithmeticBlock() {
	}

	
	protected abstract String getFormula();

	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  getFormula() ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + getFormula() ;
		default: 
			return " Number() from " + getFormula() ;
		
		}
	}


	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (0));
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1));
		return expression.toString();
	}

	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append("\n");
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (0));
		return expression.toString();
	}



	@Override
	public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinedExpression(joinId));
		expression.append("\n");
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (0));
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinedExpression(joinId));
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1));
		return expression.toString();
	}

	@Override
	public String getJoinExpressionAsFirstOperand() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append("\n");
	
		expression.append( getBlockNameAsVariable()+" : " +getArithmeticFormula (1));
		return expression.toString();
	}
}
