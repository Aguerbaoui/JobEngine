package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

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


	@Override
	public String getExpression() {
		StringBuilder expression = new StringBuilder();
		//single input
		if(threshold !=null)
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
	public String getAsFirstOperandExpression() {
		return null;
	}



	@Override
	public String getAsSecondOperandExpression() {
		return null;
	}



	@Override
	public String getJoinedExpression() {
		return null;
	}











	@Override
	public String toString() {
		return "ComparisonBlock [threshold=" + threshold + ", timePersistenceValue=" + timePersistenceValue
				+ ", timePersistenceUnit=" + timePersistenceUnit + ", ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	



	

}

