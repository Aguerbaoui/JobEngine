package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

import java.util.List;

public abstract class ArithmeticBlock extends ConditionBlock {

    protected List<Operand> operands;
    protected String operationId;

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
