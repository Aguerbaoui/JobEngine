package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;


import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class MultipleInputArithmeticBlock extends ArithmeticBlock {
	
	public MultipleInputArithmeticBlock(BlockModel blockModel) {
		super(blockModel);

	}
	

	protected MultipleInputArithmeticBlock() {
	}



	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable()+" : Number() from " + asDouble( getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( getInputRefName(i) ));
		}
		return expression.toString();
	}
 
	@Override
	public String getAsFirstOperandExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getExpression());
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable()+" : Number(doubleValue " + Keywords.toBeReplaced +") from " + asDouble( getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( getInputRefName(i) ));
		}
		return expression.toString();
	}
	
	
	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		String joinId = inputBlocks.get(0).getJoinId();
		expression.append("\n");
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable()+" : Number() from " + asDouble( getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( getInputRefName(i) ));
		}
		return expression.toString();	}

	@Override
	public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable()+" : Number() from " + asDouble( getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( getInputRefName(i) ));
		}
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable()+" : Number(doubleValue " + Keywords.toBeReplaced +") from " + asDouble( getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( getInputRefName(i) ));
		}
		return expression.toString();
	}

	@Override
	public String getJoinExpressionAsFirstOperand() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getJoinExpression());
		String joinId = inputBlocks.get(0).getJoinId();
		expression.append("\n");
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append("\n");

		}
		expression.append(getBlockNameAsVariable()+" : Number(doubleValue " + Keywords.toBeReplaced +") from " + asDouble( getInputRefName(0) ) );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append(getArithmeticFormula(0)  + asDouble( getInputRefName(i) ));
		}
		return expression.toString();
	}

	
	
	
}
