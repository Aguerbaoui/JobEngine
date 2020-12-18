package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.OperandDataType;
import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public abstract class ComparisonBlock extends ConditionBlock {

    public ComparisonBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId,
			boolean timePersistenceOn, int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, timePersistenceOn, timePersistenceValue, timePersistenceUnit);
		// TODO Auto-generated constructor stub
	}

	protected String operator;
   
    //list of instances for 1st op
    //list of instances for 2nd op example : Room (id in [1,2], $temp:temp ) , Car ( id in [4 ,6]internaltemp> $temp)

   
    }


  /*  @Override
    public String getExpression() {
        String expression = "";
        if (firstOperandDataType == OperandDataType.CLASSATTRIBUTE && secondOperandDataType == OperandDataType.CONSTANT) {
            expression = firstOperandClassName + "( " + firstOperand + operator + secondOperand + ")";
        }

        return expression;
    }
*/


