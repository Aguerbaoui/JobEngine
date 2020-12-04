package io.je.rulebuilder.components.blocks;


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
	
	
	public ComparisonBlock(DataType firstOperandDataType, String firstOperandClassName, String firstOperand,
			DataType secondOperandDataType, String secondOperandClassName, String secondOperand) {
		super();
		this.firstOperandDataType = firstOperandDataType;
		this.firstOperandClassName = firstOperandClassName;
		this.firstOperand = firstOperand;
		this.secondOperandDataType = secondOperandDataType;
		this.secondOperandClassName = secondOperandClassName;
		this.secondOperand = secondOperand;
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
