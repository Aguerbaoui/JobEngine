package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.components.Operand;
import io.je.rulebuilder.models.BlockModel;

public abstract class ArithmeticBlock extends ConditionBlock {

	List<Operand> operands;
	String operationId;
	public ArithmeticBlock(BlockModel blockModel) {
		super(blockModel.getJobEngineId(), blockModel.getProjectId(), blockModel.getRuleId(), Boolean.valueOf(blockModel.getTimePersistenceOn()), blockModel.getTimePersistenceValue(), blockModel.getTimePersistenceUnit());
		
	}
	public List<Operand> getOperands() {
		return operands;
	}
	public void setOperands(List<Operand> operands) {
		this.operands = operands;
	}
	public String getOperationId() {
		return operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}


	
}
