package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class ArctanBlock extends SingleInputArithmeticBlock {

	public ArctanBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ArctanBlock() {
		
	}
	

	
	
	@Override
	protected String getFormula() {
		return "Math.atan( " +getInputRefName(0) + ")" ;
	}



	



}
