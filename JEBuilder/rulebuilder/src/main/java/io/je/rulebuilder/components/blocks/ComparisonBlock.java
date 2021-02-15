package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public abstract class ComparisonBlock extends PersistableBlock {
	
	protected String operator;
	String threshold=null;

	public ComparisonBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(),blockModel.getBlockName(),
				blockModel.getDescription() ,
				blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());


		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			threshold = blockModel.getBlockConfiguration().getValue();
		}
		
	}
	
	
	public ComparisonBlock() {
		super();
	}

	
	private boolean singleInput() throws RuleBuildFailedException
	{
		if(inputBlocks.size()==1)
		{
			return true;
		}
		else if(inputBlocks.size()==2)
		{
			return false;
		}
		else
		{
			//TODO: remove hardcoded message
			throw new RuleBuildFailedException("Comparison block cannot have " + inputBlocks.size() + "input connexions" );
		}
	}

	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		//single input
		if(singleInput())
		{
			String inputExpression = inputBlocks.get(0).getAsFirstOperandExpression().replaceAll(Keywords.toBeReplaced, getOperator()+threshold);
			expression.append(inputExpression);

		}
		else
		{
			String firstOperand = inputBlocks.get(1).getExpression();
			expression.append(firstOperand);
			String secondOperand = inputBlocks.get(0).getAsFirstOperandExpression().replaceAll(Keywords.toBeReplaced,  getOperator() + "\\$"+getInputRefName(1) );
			expression.append(secondOperand);


		}
		return expression.toString();
	}
	
	public abstract String getOperator();

	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		//single input
		String joinId= inputBlocks.get(0).getJoinId();

		if(singleInput())
		{
			String inputExpression = inputBlocks.get(0).getJoinExpressionAsFirstOperand().replaceAll(Keywords.toBeReplaced, getOperator()+threshold);
			expression.append(inputExpression);

		}
		else
		{
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			String secondOperand = inputBlocks.get(0).getJoinExpression().replaceAll(Keywords.toBeReplaced,  getOperator() + "\\$"+getInputRefName(1) );
			expression.append(secondOperand);


		}
		return expression.toString();
	}
	

	@Override
	public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();

		//single input
		if(singleInput())
		{
			String inputExpression = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced, getOperator()+threshold);
			expression.append(inputExpression);

		}
		else
		{
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			String secondOperand = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced,  getOperator() + "\\$"+getInputRefName(1) );
			expression.append(secondOperand);


		}
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinExpressionAsFirstOperand() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String toString() {
		return "ComparisonBlock [threshold=" + threshold + ", timePersistenceValue=" + timePersistenceValue
				+ ", timePersistenceUnit=" + timePersistenceUnit + ", ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	



	

}

