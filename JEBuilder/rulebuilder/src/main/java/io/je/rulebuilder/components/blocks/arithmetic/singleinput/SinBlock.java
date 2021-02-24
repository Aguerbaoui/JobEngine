package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class SinBlock extends SingleInputArithmeticBlock {

	public SinBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  SinBlock() {
		
	}
	
	
	
	@Override
	protected String getFormula() {
		return "Math.sin( " +getInputRefName(0) + " )"  ;
	}


	



}
