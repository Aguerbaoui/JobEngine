package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

public class UnitConversionBlock extends SingleInputArithmeticBlock {

	String inputUnit;
	String outputUnit;
	public UnitConversionBlock(BlockModel blockModel) {
		super(blockModel);
		inputUnit = (String) blockModel.getBlockConfiguration().get(AttributesMapping.INPUTUNIT);
		outputUnit = (String) blockModel.getBlockConfiguration().get(AttributesMapping.OUTPUTUNIT);


	}

	private  UnitConversionBlock() {
		
	}
	

	
	@Override
	protected String getFormula() {
		return "ConversionUtilities.convert( " +inputBlocks.get(0).getReference() + ","+ inputUnit +","+ outputUnit+")" ;
	}



	



}
