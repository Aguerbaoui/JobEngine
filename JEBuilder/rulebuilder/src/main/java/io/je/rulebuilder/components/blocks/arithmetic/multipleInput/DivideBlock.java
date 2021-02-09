package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class DivideBlock extends MultipleInputArithmeticBlock {

	public DivideBlock(BlockModel blockModel) {
		super(blockModel);
	}
	
	private DivideBlock() {
		
	}

	@Override
	protected String getArithmeticFormula(int level) {		
		return " / ";
	}


}
