package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public  class LogicBlock extends PersistableBlock {

	String operator;
	public LogicBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), 
				blockModel.getBlockName(), blockModel.getDescription(),blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());
		switch(blockModel.getOperationId())
		{
		//and
		case 3001:
			operator = "";
			break;
		
		//or
		case 3002 :
			operator = "or";
			break;
		
		}
	}

	 public LogicBlock() {
		
	}

	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append(operator +"(\n");
		for(Block block : inputBlocks)
		{
			expression.append(block.getExpression());
		}
		expression.append("\n )");

		return expression.toString();
	}


	@Override
	public String getAsFirstOperandExpression() {
		// not applicable for these blocks
		return null;
	}



	@Override
	public String getJoinExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append(operator +"(\n");
		for(Block block : inputBlocks)
		{
			expression.append(block.getJoinExpression());
		}
		expression.append("\n )");

		return expression.toString();
	}

	@Override
	public String getJoinedExpression(String joinId) {
		StringBuilder expression = new StringBuilder();
		expression.append(operator +"(\n");
		for(Block block : inputBlocks)
		{
			expression.append(block.getJoinedExpression(joinId));
		}
		expression.append("\n )");

		return expression.toString();
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
