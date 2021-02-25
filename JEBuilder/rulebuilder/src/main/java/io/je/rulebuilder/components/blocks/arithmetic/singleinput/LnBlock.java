package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class LnBlock extends SingleInputArithmeticBlock {

	public LnBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  LnBlock() {
		
	}
	

	
	

	@Override
	protected String getFormula() {
		return "Math.log( " +getInputRefName(0) + ")" ;
	}

	



}
