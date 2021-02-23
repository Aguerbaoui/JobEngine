package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class PowerBlock extends SingleInputArithmeticBlock {

	String value = null;
	public PowerBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}

	private  PowerBlock() {
		
	}
	
	
	
	@Override
	protected String getFormula() {
		return "Math.pow( " +getInputRefName(0) + " , " + value + ")";
	}


	



}
