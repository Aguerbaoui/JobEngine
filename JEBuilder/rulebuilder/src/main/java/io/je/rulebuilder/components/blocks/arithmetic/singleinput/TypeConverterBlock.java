package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class TypeConverterBlock extends SingleInputArithmeticBlock {

	
	String typeToConvertTo;
	String dateFormat;
	
	public TypeConverterBlock(BlockModel blockModel) {
		super(blockModel);
		typeToConvertTo = blockModel.getBlockConfiguration().getValue();
		dateFormat = blockModel.getBlockConfiguration().getValue2();

		updateDefaultValue();

	}

	private void updateDefaultValue() {
		if(typeToConvertTo.equalsIgnoreCase("string"))
		{
			defaultType = "string";
		}else if(typeToConvertTo.equalsIgnoreCase("date"))
{
			defaultType = "date";

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
			return "JEConverter.convertTypeDate(\""+dateFormat+"\","+getInputRefName(0)+")";
		}
		else
		{
			return "Double.valueOf("+ getInputRefName(0)+")";
		}
	}

	public String getTypeToConvertTo() {
		return typeToConvertTo;
	}

	public void setTypeToConvertTo(String typeToConvertTo) {
		this.typeToConvertTo = typeToConvertTo;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}



	



}
