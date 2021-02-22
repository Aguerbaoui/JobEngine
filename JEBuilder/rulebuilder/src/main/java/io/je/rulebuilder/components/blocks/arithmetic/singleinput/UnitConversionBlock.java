package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class UnitConversionBlock extends SingleInputArithmeticBlock {

	String inputUnit;
	String outputUnit;
	public UnitConversionBlock(BlockModel blockModel) {
		super(blockModel);
		inputUnit = blockModel.getBlockConfiguration().getInputUnit();
		outputUnit = blockModel.getBlockConfiguration().getOutputUnit();


	}

	private  UnitConversionBlock() {
		
	}
	
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return " Number() from " +  "JEConverter.convert( " +getInputRefName(0) + ","+ inputUnit +","+ outputUnit+")" ;
		case 1:
			return " Number(doubleValue " + Keywords.toBeReplaced +") from " + "JEConverter.convert( " +getInputRefName(0) + ","+ inputUnit +","+ outputUnit+")" ;
		case 2:
			return "";
		default: 
			return " Number() from " +  "JEConverter.convert( " +getInputRefName(0) + ","+ inputUnit +","+ outputUnit+")" ;
		
		}
	
	}

	
	


	



}
