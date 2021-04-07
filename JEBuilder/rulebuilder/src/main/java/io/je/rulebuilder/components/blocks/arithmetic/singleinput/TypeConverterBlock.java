package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class TypeConverterBlock extends SingleInputArithmeticBlock {

	
	String typeToConvertTo;
	
	public TypeConverterBlock(BlockModel blockModel) {
		super(blockModel);
		typeToConvertTo = blockModel.getBlockConfiguration().getValue();
		updateDefaultValue();

	}

	private void updateDefaultValue() {
		if(typeToConvertTo.equalsIgnoreCase("string"))
		{
			defaultType = "string";
		}
		
	}

	private  TypeConverterBlock() {
		
	}
	


	
	@Override
	protected String getFormula() {
		if(typeToConvertTo.equalsIgnoreCase("string"))
		{
			return "String.valueOf("+getInputRefName(0)+")";
		}else if(typeToConvertTo.equalsIgnoreCase("date"))
		{
			return "";
		}
		else
		{
			return "Double.valueOf("+ getInputRefName(0)+")";
		}
	}



	



}
