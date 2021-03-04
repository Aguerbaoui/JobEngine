package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class SQRTBlock extends SingleInputArithmeticBlock {

	String value = null;
	public SQRTBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}
	
	

	public SQRTBlock() {
		super();
	
	}


	@Override
	protected String getFormula() {
		return "Math.sqrt( "+getInputRefName(0) + " ) "  ;
	}



}
