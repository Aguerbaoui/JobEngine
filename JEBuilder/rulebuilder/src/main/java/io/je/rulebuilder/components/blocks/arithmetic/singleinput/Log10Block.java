package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class Log10Block extends SingleInputArithmeticBlock {

	public Log10Block(BlockModel blockModel) {
		super(blockModel);
	}

	private  Log10Block() {
		
	}
	
	

	
	
	@Override
	protected String getFormula() {
		return "Math.log10( " +getInputRefName(0) + ")" ;
	}


	



}
