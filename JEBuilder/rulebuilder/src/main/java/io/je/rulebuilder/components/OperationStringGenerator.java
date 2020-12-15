package io.je.rulebuilder.components;

import io.je.rulebuilder.components.enumerations.SingleInputArithmeticOperation;

public class OperationStringGenerator {
	
	public static String getOperationExpression(SingleInputArithmeticOperation operation, String value)
	{
		String expression = "";
		switch(operation)
		{
		case ABS:
			expression = "Math.abs(-5)";
			break;
		case ARCCOS:
			break;
		case ARCSIN:
			break;
		case ARCTAN:
			break;
		case BIASBLOCK:
			break;
		case CEILING:
			break;
		case CHANGESIGN:
			break;
		case COS:
			break;
		case EXP:
			break;
		case FACTORIAL:
			break;
		case FLOOR:
			break;
		case GAINBLOCK:
			break;
		case LN:
			break;
		case LOG10:
			break;
		case MULTIPLICATIVEINVERSE:
			break;
		case POWER:
			break;
		case SIN:
			break;
		case SQUARE:
			break;
		case SQUAREROOT:
			break;
		case TAN:
			break;
		case TRUNCATE:
			break;
		default:
			break;
		
		}
		return expression;
	}

}
