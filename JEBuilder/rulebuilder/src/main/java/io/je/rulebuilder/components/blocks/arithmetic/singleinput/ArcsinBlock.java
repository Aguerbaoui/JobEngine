package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class ArcsinBlock extends SingleInputArithmeticBlock {

	public ArcsinBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ArcsinBlock() {
		
	}
	
	


	@Override
	protected String getFormula() {
		return "Math.asin( " +getInputRefName(0) + " )" ;
	}



	



}
