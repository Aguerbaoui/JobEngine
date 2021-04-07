package io.je.rulebuilder.components.blocks.comparison;


import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public  class InRangeBlock extends ComparisonBlock {
	
	String minRange;
	String maxRange;
	boolean includeBounds;


	public InRangeBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null)
		{
			minRange = blockModel.getBlockConfiguration().getValue();
			maxRange = blockModel.getBlockConfiguration().getValue2();
			includeBounds = Boolean.valueOf(blockModel.getBlockConfiguration().getBooleanValue());
		}

	}

	public InRangeBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	


	protected String getOperationExpression(String varMinRange , String varMaxRange )
	{
		String firstOperand = (inputBlocks.get(0) instanceof AttributeGetterBlock)? inputBlocks.get(0).getRefName():"doubleValue ";

			if(includeBounds)
			{
				return firstOperand+ ">=" + varMinRange +","+firstOperand+"=<"+varMaxRange;
			}
			else
			{
				return firstOperand+ ">" + varMinRange +","+firstOperand+"<"+varMaxRange;
			}
		
	}
	

	
	
			//throw new RuleBuildFailedException(blockName+ "cannot have " + inputBlocks.size() + "input connexions" );

/*
	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		//single input
		if(inputBlocks.size()==1)
		{
			String inputExpression = inputBlocks.get(0).getAsOperandExpression().replaceAll(Keywords.toBeReplaced, getOperationExpression(minRange,maxRange));
			expression.append(inputExpression);

		}
		else if(inputBlocks.size()==3)
		{
			String firstOperand = inputBlocks.get(1).getExpression();
			expression.append(firstOperand);
			expression.append("\n");
			String secondOperand = inputBlocks.get(0).getAsOperandExpression().replaceAll(Keywords.toBeReplaced, getOperationExpression(getInputRefName(1)) );
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
			String inputExpression = inputBlocks.get(0).getJoinExpressionAsFirstOperand().replaceAll(Keywords.toBeReplaced,getOperationExpression(minRange,maxRange));
			expression.append(inputExpression);

		}
		else
		{
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			expression.append("\n");
			String secondOperand = inputBlocks.get(0).getJoinExpression().replaceAll(Keywords.toBeReplaced,  getOperationExpression(getInputRefName(1)) );
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
			String inputExpression = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced, getOperationExpression(minRange,maxRange));
			expression.append(inputExpression);

		}
		else
		{
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			expression.append("\n");			String secondOperand = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced, getOperationExpression(getInputRefName(1)) );
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


*/


}
