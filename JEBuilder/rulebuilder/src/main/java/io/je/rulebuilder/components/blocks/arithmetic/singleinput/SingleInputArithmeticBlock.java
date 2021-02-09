package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.models.BlockModel;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {
	
	public SingleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);

	}
	

	protected SingleInputArithmeticBlock() {
	}



	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append( "$" + blockName.replaceAll("\\s+", "")+" : " +getArithmeticFormula (0));
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
	
		expression.append( "$" + blockName.replaceAll("\\s+", "")+" : " +getArithmeticFormula (1));
		return expression.toString();
	}

	@Override
	public String getJoinExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append("\n");
		expression.append( "$" + blockName.replaceAll("\\s+", "")+" : " +getArithmeticFormula (0));
		return expression.toString();
	}



	@Override
	public String getJoinedExpression(String joinId) {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinedExpression(joinId));
		expression.append("\n");
		expression.append( "$" + blockName.replaceAll("\\s+", "")+" : " +getArithmeticFormula (0));
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joinId) {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId));
		expression.append("\n");
	
		expression.append( "$" + blockName.replaceAll("\\s+", "")+" : " +getArithmeticFormula (1));
		return expression.toString();
	}

	@Override
	public String getJoinExpressionAsFirstOperand() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpressionAsFirstOperand());
		expression.append("\n");
	
		expression.append( "$" + blockName.replaceAll("\\s+", "")+" : " +getArithmeticFormula (1));
		return expression.toString();
	}
}
