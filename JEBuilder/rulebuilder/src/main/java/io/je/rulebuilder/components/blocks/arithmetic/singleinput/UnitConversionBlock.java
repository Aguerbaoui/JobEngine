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
	protected String getFormula() {
		return "JEConverter.convert( " +getInputRefName(0) + ","+ inputUnit +","+ outputUnit+")" ;
	}



	



}
