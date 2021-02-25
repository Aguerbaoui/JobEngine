package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class GainBlock extends SingleInputArithmeticBlock {

	String value = null;
	public GainBlock(BlockModel blockModel) {
		super(blockModel);
		value = (blockModel.getBlockConfiguration().getValue());
	}


	public GainBlock() {}
	
	


	@Override
	protected String getFormula() {
		return getInputRefName(0)+ " * " + value  ;
	}


}
