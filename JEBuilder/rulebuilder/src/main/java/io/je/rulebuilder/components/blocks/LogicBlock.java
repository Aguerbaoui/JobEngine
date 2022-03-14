package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public  class LogicBlock extends PersistableBlock {

	protected String operator;
	public LogicBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), 
				blockModel.getBlockName(), blockModel.getDescription(),blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit(),blockModel.getInputBlocksIds(),blockModel.getOutputBlocksIds());
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
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append("(");
		expression.append(inputBlocks.get(0).getExpression());
		expression.append(")");
		expression.append("\n");

		for(int i=1; i<inputBlocks.size();i++)
		{
			expression.append(operator);
			expression.append("\n");
			expression.append("(");
			expression.append(inputBlocks.get(i).getExpression());
			expression.append(")");
		}
		return expression.toString();
	}


	@Override
	public String getAsOperandExpression() {
		// not applicable for these blocks
		return null;
	}



	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		String joinId = inputBlocks.get(0).getJoinId();
		expression.append("\n");
		expression.append("(");
		expression.append(inputBlocks.get(0).getJoinExpression());
		expression.append(")");
		expression.append("\n");

		for(int i=1; i<inputBlocks.size();i++)
		{
			expression.append(operator);
			expression.append("\n");
			expression.append("(");
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append(")");
		}
		return expression.toString();
	
	}

	@Override
	public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		expression.append("\n");
		expression.append("(");
		expression.append(inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId));
		expression.append(")");
		expression.append("\n");

		for(int i=1; i<inputBlocks.size();i++)
		{
			expression.append(operator);
			expression.append("\n");
			expression.append("(");
			expression.append(inputBlocks.get(i).getJoinedExpression(joinId));
			expression.append(")");
		}
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
