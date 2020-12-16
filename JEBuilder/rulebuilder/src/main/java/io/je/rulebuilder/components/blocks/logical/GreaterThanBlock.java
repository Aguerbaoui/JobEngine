package io.je.rulebuilder.components.blocks.logical;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.enumerations.DataType;
import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

public class GreaterThanBlock extends ComparisonBlock {

    public GreaterThanBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId,
                            boolean timePersistenceOn, int timePersistenceValue, TimePersistenceUnit timePersistenceUnit,
                            DataType firstOperandDataType, String firstOperandClassName, String firstOperand,
                            DataType secondOperandDataType, String secondOperandClassName, String secondOperand, String operator) {
        super(jobEngineElementID, jobEngineProjectID, ruleId, timePersistenceOn, timePersistenceValue, timePersistenceUnit,
                firstOperandDataType, firstOperandClassName, firstOperand, secondOperandDataType, secondOperandClassName,
                secondOperand, operator);
        // TODO Auto-generated constructor stub
    }


}
