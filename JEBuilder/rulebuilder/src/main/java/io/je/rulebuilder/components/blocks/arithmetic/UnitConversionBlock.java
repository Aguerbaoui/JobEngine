package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class UnitConversionBlock extends ArithmeticBlock {

	String value = null;
	public UnitConversionBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	public UnitConversionBlock() {
		
	}
	
	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("$"+blockName +" : Number() from " + " $"+inputBlocks.get(0).getBlockName() + " + " + value );
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("$"+blockName +" : Number(doubleValue " + Keywords.toBeReplaced +") from " + " $"+inputBlocks.get(0).getBlockName() + " + " + value );
		return expression.toString();
	}

	@Override
	public String getAsSecondOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("$"+blockName +" : Number( " + Keywords.toBeReplaced +" doubleValue ) from " + " $"+inputBlocks.get(0).getBlockName() + " + " + value );
		return expression.toString();
	}

	@Override
	public String getJoinedExpression() {
		// TODO Auto-generated method stub
		return null;
	}



}
