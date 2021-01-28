package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class GainBlock extends ArithmeticBlock {

	String value = null;
	public GainBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("$"+blockName.trim().trim() +" : Number() from " + " $"+getInputRefName(0)+ " * " + value );
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
	
		expression.append("$"+blockName.trim() +" : Number(doubleValue " + Keywords.toBeReplaced +") from " + " $"+getInputRefName(0) + " * " + value );
		return expression.toString();
	}

	@Override
	public String getAsSecondOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		
		expression.append("$"+blockName.trim() +" : Number( " + Keywords.toBeReplaced +" doubleValue ) from " + " $"+getInputRefName(0) + " * " + value );
		return expression.toString();
	}

	@Override
	public String getJoinedExpression() {
		// TODO Auto-generated method stub
		return null;
	}



}
