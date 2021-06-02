package io.je.rulebuilder.components.blocks.comparison;


import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.arithmetic.singleinput.SingleInputArithmeticBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public  class ComparisonBlock extends PersistableBlock {
	
	protected String operator;
	String threshold=null;
	String maxRange=null;
	boolean includeBounds=false;
	boolean formatToString=false;

	
	
	protected ComparisonBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription, int timePersistenceValue, String timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription, timePersistenceValue,
				timePersistenceUnit);
		// TODO Auto-generated constructor stub
	}
	public ComparisonBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(),blockModel.getBlockName(),
				blockModel.getDescription() ,
				blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());

		

		if(blockModel.getBlockConfiguration()!=null )
		{
			threshold = blockModel.getBlockConfiguration().getValue();
			maxRange = blockModel.getBlockConfiguration().getValue2();
			includeBounds = Boolean.valueOf(blockModel.getBlockConfiguration().getBooleanValue());
		}
		
		operator = getOperatorByOperationId(blockModel.getOperationId());
		formatToString = (blockModel.getOperationId()>=2007 && blockModel.getOperationId()<=2015);
		
	}
	protected String getOperationExpression()
	{
		String firstOperand = null;
		if(inputBlocks.get(0) instanceof AttributeGetterBlock)
		{
			firstOperand = inputBlocks.get(0).getRefName();
		}
		else
		{
			if(inputBlocks.get(0) instanceof SingleInputArithmeticBlock)
			{
				SingleInputArithmeticBlock input = (SingleInputArithmeticBlock) inputBlocks.get(0);
				if(input.getDefaultType().equals("string"))
				{
					firstOperand = "this ";
				}
				else
				{
					firstOperand = "doubleValue ";
				}
						
			}
		}
		return firstOperand+ getOperator() + formatOperator(threshold);

	}
	
	public ComparisonBlock() {
		super();
	}

	
	protected void setParameters() {
		if(threshold==null)
		{
			threshold=getInputRefName(1);
		}
		
	}
	
	

	@Override
	public String getExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();

		checkBlockConfiguration();
		setParameters();

		// single input
		if (inputBlocks.size() == 1) {
			String inputExpression = inputBlocks.get(0).getAsOperandExpression().replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(inputExpression);

		} else if (inputBlocks.size() == 3) {
			String firstOperand = inputBlocks.get(1).getExpression();
			expression.append(firstOperand);
			expression.append("\n");
			String thirdOperand = inputBlocks.get(2).getExpression();
			expression.append(thirdOperand);
			expression.append("\n");
			String secondOperand = inputBlocks.get(0).getAsOperandExpression().replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(secondOperand);

		} else if (inputBlocks.size() == 2) {
			String firstOperand = inputBlocks.get(1).getExpression();
			expression.append(firstOperand);
			String secondOperand = "";
			expression.append("\n");
			secondOperand = inputBlocks.get(0).getAsOperandExpression().replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(secondOperand);
		}
		return expression.toString();
	}

	protected void checkBlockConfiguration() throws RuleBuildFailedException {
		if (threshold == null && inputBlocks.size() != 2) {
			throw new RuleBuildFailedException(blockName + " is not configured properly");
		}
		
	}
	public String getOperator() {
		return operator;
	}

	@Override
	public String getJoinExpression() throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		String joinId = inputBlocks.get(0).getJoinId();
		checkBlockConfiguration();
		setParameters();

		// single input
		if (inputBlocks.size() == 1) {
			String inputExpression = inputBlocks.get(0).getJoinExpressionAsFirstOperand().replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(inputExpression);

		} else if (inputBlocks.size() == 3) {
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			expression.append("\n");
			String thirdOperand = inputBlocks.get(2).getJoinedExpression(joinId);
			expression.append(thirdOperand);
			expression.append("\n");
			String secondOperand = inputBlocks.get(0).getJoinExpression().replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(secondOperand);

		} else if (inputBlocks.size() == 2) {
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			String secondOperand = "";
			expression.append("\n");
			secondOperand = inputBlocks.get(0).getJoinExpression().replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(secondOperand);
		}
		return expression.toString();
	}

	@Override
	public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
		StringBuilder expression = new StringBuilder();
		checkBlockConfiguration();
		setParameters();

		// single input
		if (inputBlocks.size() == 1) {
			String inputExpression = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(inputExpression);

		} else if (inputBlocks.size() == 3) {
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			expression.append("\n");
			String thirdOperand = inputBlocks.get(2).getJoinedExpression(joinId);
			expression.append(thirdOperand);
			expression.append("\n");
			String secondOperand = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(secondOperand);

		} else if (inputBlocks.size() == 2) {
			String firstOperand = inputBlocks.get(1).getJoinedExpression(joinId);
			expression.append(firstOperand);
			String secondOperand = "";
			expression.append("\n");
			secondOperand = inputBlocks.get(0).getJoinedExpressionAsFirstOperand(joinId).replaceAll(Keywords.toBeReplaced,
					getOperationExpression());
			expression.append(secondOperand);
		}
		return expression.toString();
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joindId) {
		return null;
	}

	@Override
	public String getJoinExpressionAsFirstOperand() {
		return null;
	}


	protected String getMaxRange() {
		return maxRange;
	}

	protected void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}

	protected boolean isIncludeBounds() {
		return includeBounds;
	}

	protected void setIncludeBounds(boolean includeBounds) {
		this.includeBounds = includeBounds;
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
			return ">";
		case 2015:
			return "<";
		default:
			return null;
		}
		
	}


	public String formatOperator(String operator) {
		return formatToString? "\""+ operator +"\"":operator;
	}

	

}

