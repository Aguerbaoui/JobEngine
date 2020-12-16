package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.Operand;
import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.enumerations.SingleInputArithmeticOperation;
import io.je.rulebuilder.models.BlockModel;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {

    Operand operand;
    SingleInputArithmeticOperation operation;

    public SingleInputArithmeticBlock(BlockModel blockModel) {
        super(blockModel);
        String operandId = blockModel.getOperandIds().get(0);
        //operand = OperandInventory.getOperand(operandId);


    }

    @Override
    public String getExpression() {
        String expression = "";
        switch (operand.getDataType()) {
            case ARITHMETICRESULT:
                break;
            case CLASSATTRIBUTE:
                break;
            case CONSTANT:
                break;
            case VARIABLE:
                break;
            default:
                break;

        }
        return expression;
    }


}
