package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class TanBlock extends SingleInputArithmeticBlock {

	public TanBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  TanBlock() {
		
	}
	
	
	@Override
	protected String getFormula() {
		return "Math.tan( " +getInputRefName(0) + ")" ;
	}


	



}
