package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;


import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public abstract class MultipleInputArithmeticBlock extends ArithmeticBlock {
	
	public MultipleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);

	}
	

	protected MultipleInputArithmeticBlock() {
	}



	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getExpression());
			expression.append("\n");

		}
		expression.append("$"+blockName +" : Number() from " + asDouble( "$"+getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( "$"+getInputRefName(i) ));
		}
		return expression.toString();
	}
 
	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getExpression());
			expression.append("\n");

		}
		expression.append("$"+blockName +" : Number(doubleValue " + Keywords.toBeReplaced +") from " + asDouble( "$"+getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( "$"+getInputRefName(i) ));
		}
		return expression.toString();
	}
	
	
	@Override
	public String getJoinExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpression(String joinId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joinId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinExpressionAsFirstOperand() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
