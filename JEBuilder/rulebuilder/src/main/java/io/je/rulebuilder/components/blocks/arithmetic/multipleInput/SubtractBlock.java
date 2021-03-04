package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class SubtractBlock extends MultipleInputArithmeticBlock {

	public SubtractBlock(BlockModel blockModel) {
		super(blockModel);
	}
	
	private SubtractBlock() {
		
	}

	@Override
	protected String getArithmeticFormula(int level) {		
		return " - ";
	}


}
