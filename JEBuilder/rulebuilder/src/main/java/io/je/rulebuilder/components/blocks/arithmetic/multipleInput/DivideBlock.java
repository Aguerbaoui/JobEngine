package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class DivideBlock extends MultipleInputArithmeticBlock {

	public DivideBlock(BlockModel blockModel) {
		super(blockModel);
		stopExecutionIfInvalidInput = true;
	}
	
	private DivideBlock()
	{
		stopExecutionIfInvalidInput = true;
	}

	@Override
	protected String getArithmeticFormula(int level,String type) {
		return "MathUtilities.divide( "  ;

	}
	
	@Override
	protected String evaluateExecution(String...inputs) {
		return "eval(JEMathUtils.DivisionByZero(\""+this.jobEngineProjectID+"\",\""+this.ruleId+"\",\""+this.blockName+"\","+inputs[0]+"))\n";
	}

}
