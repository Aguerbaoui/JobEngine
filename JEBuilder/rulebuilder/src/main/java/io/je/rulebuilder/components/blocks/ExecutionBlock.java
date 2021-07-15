package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class ExecutionBlock extends Block {

	public ExecutionBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription());
	}

	
	
	public ExecutionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription);
	}



	public ExecutionBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	@Override
	public String getAsOperandExpression() {
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
	public String getJoinExpressionAsFirstOperand() {
		// TODO Auto-generated method stub
		return null;
	}



}
