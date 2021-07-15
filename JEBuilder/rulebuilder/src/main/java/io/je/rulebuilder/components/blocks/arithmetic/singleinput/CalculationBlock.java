package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class CalculationBlock extends SingleInputArithmeticBlock {
	
	int operationId= 0;
	protected String calculatorReference="JECalculator";
	String value = null;

	public CalculationBlock(BlockModel blockModel) {
		super(blockModel);
		operationId = blockModel.getOperationId();
		if(blockModel.getBlockConfiguration()!=null)
		{
			value = (blockModel.getBlockConfiguration().getValue());
		}
	}

	private  CalculationBlock() {
		
		
	}

	@Override
	protected String getFormula() {
		StringBuilder formula= new StringBuilder();
		formula.append(calculatorReference);
		formula.append(".");
		String method = getMethod();
		formula.append(method);
		return formula.toString();
	}
	
	
	private String getMethod()
	{
		switch(operationId)
		{
				// Factorial
				case 1005:
					return "factorial( " + getInputRefName(0)+ ")";
				// Square
				case 1006:
					return"square( " +getInputRefName(0) +")";
				// SquareRoot
				case 1007:
					return "sqrt( " + getInputRefName(0)+ ")";
				// Power
				case 1008:
					return "power( "  + getInputRefName(0)+ ", " +  value +")";
				// change sign
				case 1009:
					return "changeSign( " + getInputRefName(0)+ ")";
				//Bias	
				case 1010:
					return getInputRefName(0)+ " + " + value ;
				//gain
				case 1011:
					return getInputRefName(0)+ " * " + value ;
				//multiplicative inverse	
				case 1012:
					return "multiplicativeInverse( " + getInputRefName(0)+ ")" ;
				//abs
				case 1013:
					return "abs( " +getInputRefName(0) + ")";
				//exp	
				case 1014:
					return "exp( " + getInputRefName(0)+ ")";
				//log10	
				case 1015:
					return "log10( " + getInputRefName(0)+ ")";
				//tan	
				case 1016:
					return "tan( " + getInputRefName(0)+ ")";
				//atan	
				case 1017:
					return "atan( " + getInputRefName(0)+ ")";
				//acos	
				case 1018:
					return "acos( " + getInputRefName(0)+ ")";
				//asin
				case 1019:
					return "asin( " + getInputRefName(0)+ ")";
				//floor	
				case 1020:
					return "floor( " + getInputRefName(0)+ ")";
				//truncate (round)	
				case 1021:
					return "truncate( " + getInputRefName(0)+ ")";
				//ceiling	
				case 1022:
					return "ceil( " + getInputRefName(0)+ ")";
				//sin	
				case 1023:
					return "sin( " + getInputRefName(0)+ ")";
				//cos	
				case 1024:
					return "cos( " + getInputRefName(0)+ ")";
				//ln	
				case 1025:
					return "ln( " + getInputRefName(0)+ ")";
				//length	
				case 1026:
					return getInputRefName(0)+".length()" ;
				default :
					return getInputRefName(0);

		}

	}

}
