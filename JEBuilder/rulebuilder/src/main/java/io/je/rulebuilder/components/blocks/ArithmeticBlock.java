package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

public abstract class ArithmeticBlock extends ConditionBlock {
	
	//a random variable name used to reference the block's input TODO: refactor to functional block
	String operationIdentifier;

	public ArithmeticBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
		operationIdentifier = "randomvalue";

	}

	@Override
	public String toString() {
		return "ArithmeticBlock [ruleId=" + ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks=" + outputBlocks
				+ ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID=" + jobEngineProjectID
				+ ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
		
		
	}
	
	
	public abstract String getExpression(String expression);

	public String getOperationIdentifier() {
		return operationIdentifier;
	}

	public void setOperationIdentifier(String operationIdentifier) {
		this.operationIdentifier = operationIdentifier;
	}

	

}
