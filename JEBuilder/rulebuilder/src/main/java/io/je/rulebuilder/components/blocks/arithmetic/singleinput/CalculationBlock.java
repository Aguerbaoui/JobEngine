package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class CalculationBlock extends SingleInputArithmeticBlock {
	
	int operationId= 0;
	protected String calculatorReference="MathUtilities.";
	String value = null;

	public CalculationBlock(BlockModel blockModel) {
		super(blockModel);
		operationId = blockModel.getOperationId();
		if(blockModel.getBlockConfiguration()!=null)
		{
			value = String.valueOf(blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));
		}
	}

	private  CalculationBlock() {
		
		
	}

	@Override
	protected String getFormula() {
		StringBuilder formula= new StringBuilder();
		
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
					
					return calculatorReference+"factorial( " + getInputReferenceByOrder(0)+ ")";
				// Square
				case 1006:
					return calculatorReference+"square( " +getInputReferenceByOrder(0) +")";
				// SquareRoot
				case 1007:
					return calculatorReference+"sqrt( " + getInputReferenceByOrder(0)+ ")";
				// Power
				case 1008:
					return calculatorReference+"power( "  + getInputReferenceByOrder(0)+ ", " +  value +")";
				// change sign
				case 1009:
					return calculatorReference+"changeSign( " + getInputReferenceByOrder(0)+ ")";
				//Bias	
				case 1010:
					//return calculatorReference+"bias( " + getInputReferenceByOrder(0)+ ","+value+")";

					return getInputReferenceByOrder(0)+ " + " + value ;
				//gain
				case 1011:
					return getInputReferenceByOrder(0)+ " * " + value ;
				//multiplicative inverse	
				case 1012:
					return calculatorReference+"multiplicativeInverse( " + getInputReferenceByOrder(0)+ ")" ;
				//abs
				case 1013:
					return calculatorReference+"abs( " +getInputReferenceByOrder(0) + ")";
				//exp	
				case 1014:
					return calculatorReference+"exp( " + getInputReferenceByOrder(0)+ ")";
				//log10	
				case 1015:
					return calculatorReference+"log10( " + getInputReferenceByOrder(0)+ ")";
				//tan	
				case 1016:
					return calculatorReference+"tan( " + getInputReferenceByOrder(0)+ ")";
				//atan	
				case 1017:
					return calculatorReference+"atan( " + getInputReferenceByOrder(0)+ ")";
				//acos	
				case 1018:
					return calculatorReference+"acos( " + getInputReferenceByOrder(0)+ ")";
				//asin
				case 1019:
					return calculatorReference+"asin( " + getInputReferenceByOrder(0)+ ")";
				//floor	
				case 1020:
					return calculatorReference+"floor( " + getInputReferenceByOrder(0)+ ")";
				//truncate (round)	
				case 1021:
					return calculatorReference+"truncate( " + getInputReferenceByOrder(0)+ ")";
				//ceiling	
				case 1022:
					return calculatorReference+"ceil( " + getInputReferenceByOrder(0)+ ")";
				//sin	
				case 1023:
					return calculatorReference+"sin( " + getInputReferenceByOrder(0)+ ")";
				//cos	
				case 1024:
					return calculatorReference+"cos( " + getInputReferenceByOrder(0)+ ")";
				//ln	
				case 1025:
					return calculatorReference+"ln( " + getInputReferenceByOrder(0)+ ")";
				//length	
				case 1026:
					return getInputReferenceByOrder(0)+".length()" ;
				default :
					return getInputReferenceByOrder(0);

		}

	}

	
	@Override
	protected String evaluateExecution(String...inputs) {
		switch(operationId) {
		
		//factorial x>=0 && x<=20
		case 1005:
			return "eval(JEMathUtils.factorialConstraint(\""+this.jobEngineProjectID+"\",\""+this.ruleId+"\",\""+this.blockName+"\","+inputs[0]+"))\n";

		//sqrt x>=0
		case 1007:
			return "eval(JEMathUtils.positive(\""+this.jobEngineProjectID+"\",\""+this.ruleId+"\",\""+this.blockName+"\","+inputs[0]+"))\n";
		//ln x>0
		case 1025 :
			return "eval(JEMathUtils.strictlyPositive(\""+this.jobEngineProjectID+"\",\""+this.ruleId+"\",\""+this.blockName+"\","+inputs[0]+"))\n";
		default:
			return "";
		}
	}

}
