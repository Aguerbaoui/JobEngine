package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.DataType;
import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

/* 
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public abstract class ComparisonBlock extends LogicalBlock {
	
	DataType firstOperandDataType;
	String firstOperandClassName;
	String firstOperand;
	DataType secondOperandDataType;
	String secondOperandClassName;
	String secondOperand;
	protected String operator;
	//list of instances for 1st op 
	//list of instances for 2nd op example : Room (id in [1,2], $temp:temp ) , Car ( id in [4 ,6]internaltemp> $temp)  
	
	public ComparisonBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId,
			boolean timePersistenceOn, int timePersistenceValue, TimePersistenceUnit timePersistenceUnit,
			DataType firstOperandDataType, String firstOperandClassName, String firstOperand,
			DataType secondOperandDataType, String secondOperandClassName, String secondOperand, String operator) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, timePersistenceOn, timePersistenceValue,
				timePersistenceUnit);
		this.firstOperandDataType = firstOperandDataType;
		this.firstOperandClassName = firstOperandClassName;
		this.firstOperand = firstOperand;
		this.secondOperandDataType = secondOperandDataType;
		this.secondOperandClassName = secondOperandClassName;
		this.secondOperand = secondOperand;
		this.operator = operator;
	}
	



	
	
	@Override
	public String getExpression()
	{
		String expression = "";
		if(firstOperandDataType==DataType.CLASSATTRIBUTE && secondOperandDataType == DataType.CONSTANT )
		{
			expression = firstOperandClassName + "( "+ firstOperand + operator + secondOperand+  ")";
		}
		
		return expression;
	}


}
