package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class UnitConversionBlock extends SingleInputArithmeticBlock {

	String value = null;
	public UnitConversionBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	public UnitConversionBlock() {
		
	}
	
	@Override
	protected String getArithmeticFormula(int level) {
		switch(level)
		{
		case 0:
			return "" ;
		case 1:
			return "" ;
		case 2:
			return "";
		default: 
			return ""  ;
		
		}
	
	}


}
