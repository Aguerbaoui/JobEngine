package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class BiasBlock extends SingleInputArithmeticBlock {

	String value = null;
	public BiasBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}
	
	public BiasBlock() {
	
	}



	@Override
	protected String getFormula() {
		return getInputRefName(0)+ " + " + value ;
	}


}
