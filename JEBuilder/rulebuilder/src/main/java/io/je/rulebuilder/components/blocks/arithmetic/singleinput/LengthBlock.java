package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class LengthBlock extends SingleInputArithmeticBlock {

	public LengthBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  LengthBlock() {
		
	}

	
	@Override
	protected String getFormula() {
		return getInputRefName(0)+".length()" ;
	}



	



}
