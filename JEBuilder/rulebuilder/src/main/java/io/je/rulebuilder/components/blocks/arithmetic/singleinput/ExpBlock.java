package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class ExpBlock extends SingleInputArithmeticBlock {

	public ExpBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ExpBlock() {
		
	}
	

	
	

	@Override
	protected String getFormula() {
		return "Math.exp( " +getInputRefName(0) + ")" ;
	}

	



}
