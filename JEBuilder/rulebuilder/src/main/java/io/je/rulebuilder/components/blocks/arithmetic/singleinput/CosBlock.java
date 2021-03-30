package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.models.BlockModel;

public class CosBlock extends SingleInputArithmeticBlock {

	public CosBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  CosBlock() {
		
	}
	
	
	
	@Override
	protected String getFormula() {
		return "Math.cos( " +getInputRefName(0) + ")" ;
	}



	



}
