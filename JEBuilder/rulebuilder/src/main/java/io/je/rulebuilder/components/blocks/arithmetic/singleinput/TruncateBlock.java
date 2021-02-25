package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class TruncateBlock extends SingleInputArithmeticBlock {

	public TruncateBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  TruncateBlock() {
		
	}
	


	
	@Override
	protected String getFormula() {
		return "Math.round( " +getInputRefName(0) + ")" ;
	}



	



}
