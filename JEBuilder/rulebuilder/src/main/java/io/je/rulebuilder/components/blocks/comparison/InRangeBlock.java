package io.je.rulebuilder.components.blocks.comparison;


import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.models.BlockModel;

public  class InRangeBlock extends ConditionBlock {
	
	String minRange;
	String maxRange;
	boolean includeBounds;


	public InRangeBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription());
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
	
	@Override
	public String getAsFirstOperandExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpression(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinExpressionAsFirstOperand() {
		// TODO Auto-generated method stub
		return null;
	}

}
