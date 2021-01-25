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
	public String getAsSecondOperandExpression() {
		// not applicable for these blocks
		return null;
	}



	@Override
	public String getJoinedExpression() {
		StringBuilder expression = new StringBuilder();
		expression.append(operator +"(\n");
		for(Block block : inputBlocks)
		{
			expression.append(block.getJoinedExpression());
		}
		expression.append("\n )");

		return expression.toString();
	}







}
