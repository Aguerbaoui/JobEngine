package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class MultiplyBlock extends ArithmeticBlock {

	public MultiplyBlock(BlockModel blockModel) {
		super(blockModel);
	}
	
	private MultiplyBlock() {
		
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
			expression.append(" * "  + asDouble( "$"+getInputRefName(i) ));
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
			expression.append(" * "  + asDouble( "$"+getInputRefName(i) ));
		}
		return expression.toString();
	}

	@Override
	public String getAsSecondOperandExpression() {
		return null;

	}

	@Override
	public String getJoinedExpression() {
		// TODO Auto-generated method stub
		return null;
	}
}
