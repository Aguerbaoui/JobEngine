package io.je.rulebuilder.components.blocks.logical;

import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.components.blocks.DataType;
import io.je.rulebuilder.components.blocks.operators.Operator;

public class GreaterThanBlock extends ComparisonBlock {

	public GreaterThanBlock(DataType firstOperandDataType, String firstOperandClassName, String firstOperand,
			DataType secondOperandDataType, String secondOperandClassName, String secondOperand) {
		super(firstOperandDataType, firstOperandClassName, firstOperand, secondOperandDataType, secondOperandClassName,
				secondOperand);
		operator = ">";
	}
	
	
	
	
	
	
	

}
