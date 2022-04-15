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
					
					return calculatorReference+"factorial( " + inputBlocks.get(0).getReference()+ ")";
				// Square
				case 1006:
					return calculatorReference+"square( " +inputBlocks.get(0).getReference() +")";
				// SquareRoot
				case 1007:
					return calculatorReference+"sqrt( " + inputBlocks.get(0).getReference()+ ")";
				// Power
				case 1008:
					return calculatorReference+"power( "  + inputBlocks.get(0).getReference()+ ", " +  value +")";
				// change sign
				case 1009:
					return calculatorReference+"changeSign( " + inputBlocks.get(0).getReference()+ ")";
				//Bias	
				case 1010:
					//return calculatorReference+"bias( " + inputBlocks.get(0).getReference()+ ","+value+")";

					return inputBlocks.get(0).getReference()+ " + " + value ;
				//gain
				case 1011:
					return inputBlocks.get(0).getReference()+ " * " + value ;
				//multiplicative inverse	
				case 1012:
					return calculatorReference+"multiplicativeInverse( " + inputBlocks.get(0).getReference()+ ")" ;
				//abs
				case 1013:
					return calculatorReference+"abs( " +inputBlocks.get(0).getReference() + ")";
				//exp	
				case 1014:
					return calculatorReference+"exp( " + inputBlocks.get(0).getReference()+ ")";
				//log10	
				case 1015:
					return calculatorReference+"log10( " + inputBlocks.get(0).getReference()+ ")";
				//tan	
				case 1016:
					return calculatorReference+"tan( " + inputBlocks.get(0).getReference()+ ")";
				//atan	
				case 1017:
					return calculatorReference+"atan( " + inputBlocks.get(0).getReference()+ ")";
				//acos	
				case 1018:
					return calculatorReference+"acos( " + inputBlocks.get(0).getReference()+ ")";
				//asin
				case 1019:
					return calculatorReference+"asin( " + inputBlocks.get(0).getReference()+ ")";
				//floor	
				case 1020:
					return calculatorReference+"floor( " + inputBlocks.get(0).getReference()+ ")";
				//truncate (round)	
				case 1021:
					return calculatorReference+"truncate( " + inputBlocks.get(0).getReference()+ ")";
				//ceiling	
				case 1022:
					return calculatorReference+"ceil( " + inputBlocks.get(0).getReference()+ ")";
				//sin	
				case 1023:
					return calculatorReference+"sin( " + inputBlocks.get(0).getReference()+ ")";
				//cos	
				case 1024:
					return calculatorReference+"cos( " + inputBlocks.get(0).getReference()+ ")";
				//ln	
				case 1025:
					return calculatorReference+"ln( " + inputBlocks.get(0).getReference()+ ")";
				//length	
				case 1026:
					return inputBlocks.get(0).getReference()+".length()" ;
				default :
					return inputBlocks.get(0).getReference();

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
