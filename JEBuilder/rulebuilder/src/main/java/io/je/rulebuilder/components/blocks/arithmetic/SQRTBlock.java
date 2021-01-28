package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class SQRTBlock extends ArithmeticBlock {

	String value = null;
	public SQRTBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}
	
	

	public SQRTBlock() {
		super();
	
	}



	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("$"+blockName.replaceAll("\\s+","") +" : Number() from " + "Math.sqrt( $"+getInputRefName(0) + " ) "  );
		return expression.toString();
	}

	@Override
	public String getAsFirstOperandExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append("\n");
		expression.append("\n");
		expression.append("$"+blockName.replaceAll("\\s+","") +" : Number(doubleValue " + Keywords.toBeReplaced +") from " + "Math.sqrt( $"+getInputRefName(0)+ " ) " );
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
