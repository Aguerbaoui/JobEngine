package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class FloorBlock extends SingleInputArithmeticBlock {

	public FloorBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  FloorBlock() {
		
	}
	


	
	
	@Override
	protected String getFormula() {
		return "Math.floor(" +getInputRefName(0) + ")" ;
	}


	



}
