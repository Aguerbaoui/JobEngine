package io.je.rulebuilder.components.blocks;


import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public  class ComparisonBlock extends PersistableBlock {
	
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
		
		operator = getOperatorByOperationId(blockModel.getOperationId());
		
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
			throw new RuleBuildFailedException(JEMessages.INPUT_CONNECTION1 + inputBlocks.size() + JEMessages.INPUT_CONNECTION2 );
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
	
	public String getOperator()
	{
		return operator;
	}

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
	
	
	public String getOperatorByOperationId(int operationId)
	{
		switch(operationId)
		{
		case 2001:
			return "==";
		case 2002:
			return "!=";
		case 2003:
			return ">";
		case 2004:
			return ">=";
		case 2005:
			return "<";
		case 2006:
			return "<=";
		case 2007:
			return " contains ";
		case 2008:
			return " not contains ";
		case 2009:
			return " matches ";
		case 2010:
			return " not matches ";
		case 2011:
			return " str[startsWith] ";
		case 2012:
			return " str[endsWith] ";
		case 2013:
			return "";
		case 2014:
			return "<";
		case 2015:
			return ">";
		}
		
		return null;
	}


	

}

