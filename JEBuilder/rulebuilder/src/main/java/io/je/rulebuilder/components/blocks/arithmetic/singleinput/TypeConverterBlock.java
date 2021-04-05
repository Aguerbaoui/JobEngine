package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class TypeConverterBlock extends SingleInputArithmeticBlock {

	
	String valueToConvertTo;
	public TypeConverterBlock(BlockModel blockModel) {
		super(blockModel);
		valueToConvertTo = blockModel.getBlockConfiguration().getValue();
		updateDefaultValue();

	}

	private void updateDefaultValue() {
		if(valueToConvertTo.equalsIgnoreCase("string"))
		{
			defaultType = "string";
		}
		
	}

	private  TypeConverterBlock() {
		
	}
	


	
	@Override
	protected String getFormula() {
		if(valueToConvertTo.equalsIgnoreCase("string"))
		{
			return "String.valueOf("+getInputRefName(0)+")";
		}
		else
		{
			return "Double.valueOf("+ getInputRefName(0)+")";
		}
	}



	



}
