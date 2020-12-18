package io.je.rulebuilder.components.blocks.arithmetic;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.Operand;
import io.je.rulebuilder.components.enumerations.SingleInputArithmeticOperation;
import io.je.rulebuilder.models.BlockModel;


/*
 * Arithmetic blocks with one input
 */
public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {

    
    SingleInputArithmeticOperation operation;

    public SingleInputArithmeticBlock(BlockModel blockModel) {
        super(blockModel);
        String operandId = blockModel.getOperandIds().get(0);
        //operand = OperandInventory.getOperand(operandId);


    }

    @Override
    public String getExpression() {
        String expression = "";
        switch (operands.get(0).getOperandDataType()) {
		case ARITHMETICBLOCK:
			break;
		case CLASSATTRIBUTE:
			break;
		case CONSTANT:
			break;
		case LOGICBLOCK:
			break;
		case VARIABLE:
			break;
		default:
			break;
           

        }
        return expression;
    }


}
