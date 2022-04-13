package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class MultipleInputArithmeticBlock extends ArithmeticBlock {

	public MultipleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);
	}

	
	public MultipleInputArithmeticBlock()
	{
		
	}
	
	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = generateAllPreviousBlocksExpressions();
		expression.append(generateBlockExpression(false));
		return expression.toString();
	}

	

	

	private StringBuilder generateAllPreviousBlocksExpressions() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i = 0; i < inputBlocks.size(); i++) {
			expression.append(inputBlocks.get(i).getExpression());
			expression.append("\n");

		}
		return expression;
	}
	
	private String generateBlockExpression(boolean comparable)
	{
		String comparableExpression = " : Number() from ";
		if(comparable)
		{
			comparableExpression = " : Number(doubleValue " + Keywords.toBeReplaced + ") from ";
		}
		StringBuilder expression = new StringBuilder();
		
		expression.append(getBlockNameAsVariable() + comparableExpression);
		expression.append(getArithmeticFormula(0,"number") + asDouble(getInputRefName(0)));
		for (int i = 1; i < inputBlocks.size(); i++) {
			expression.append(" , " + asDouble(getInputRefName(i)));
		}
		expression.append(")");
		if(stopExecutionIfInvalidInput)
		{
			expression.append("\n"+evaluateExecution(asDouble(getInputRefName(1))));
		}
		return expression.toString();
		
		
	}

}
