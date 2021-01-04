package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.models.BlockModel;

public class FunctionBlock extends ArithmeticBlock {

	int operationId;
	public FunctionBlock(BlockModel blockModel,int operationId) {
		super(blockModel);
		this.operationId=operationId;
	}
	@Override
	public String getExpression() {
		return "$"+jobEngineElementID +" : Number() from "+getFunction(operationId) + "("+getOperationIdentifier()+")";

	}
	@Override
	public String getComparableExpression(String constraint) {
		
		return "$"+jobEngineElementID +" : Number (Double " + constraint + " ) from "+getFunction(operationId) + "((Double)"+getOperationIdentifier()+")";
	}
	@Override
	public String getExpression(String expression) {
		
		return null;
	}

	private String getFunction(int operationId)
	{
		switch(operationId)
		{
		case 1005:
			//TODO: add calulaction library
			return "";
		case 1006:
			return "Math.pow";
		case 1007:
			return "Math.sqrt";
		case 1009:
			return "";
		}
		return null;
	}

}
