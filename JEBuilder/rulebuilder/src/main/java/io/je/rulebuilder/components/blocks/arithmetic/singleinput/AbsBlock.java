package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class AbsBlock extends SingleInputArithmeticBlock {

	public AbsBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  AbsBlock() {
		
	}
	@Override
	protected String getFormula() {
		return "Math.abs( " +getInputRefName(0) + ")" ;
	}

	
	


	



}
