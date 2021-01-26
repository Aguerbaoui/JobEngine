package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class SumBlock extends ArithmeticBlock {

	public SumBlock(BlockModel blockModel) {
		super(blockModel);
	}

	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append("$"+blockName +" : Number() from " + " $"+inputBlocks.get(0).getBlockName()  );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append("+ "  + " $"+inputBlocks.get(i).getBlockName());
		}
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		for (int i=0;i<inputBlocks.size();i++)
		{
			expression.append(inputBlocks.get(0).getExpression());
			expression.append("\n");

		}
		expression.append("$"+blockName +" : Number(doubleValue " + Keywords.toBeReplaced +") from " +" $"+inputBlocks.get(0).getBlockName()  );
		for (int i=1;i<inputBlocks.size();i++)
		{
			expression.append("+ "  + " $"+inputBlocks.get(i).getBlockName());
		}
		return expression.toString();
	}

	@Override
	public String getAsSecondOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("$"+blockName +" : Number( " + Keywords.toBeReplaced +" doubleValue ) from " + " $"+inputBlocks.get(0).getBlockName() + " + " + " $"+inputBlocks.get(1).getBlockName() );
		return expression.toString();
	}

	@Override
	public String getJoinedExpression() {
		// TODO Auto-generated method stub
		return null;
	}
}
