package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class CeilingBlock extends SingleInputArithmeticBlock {

	public CeilingBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  CeilingBlock() {
		
	}
	
	


	
	@Override
	protected String getFormula() {
		return "Math.ceil( " +getInputRefName(0) + ")" ;
	}



	



}
