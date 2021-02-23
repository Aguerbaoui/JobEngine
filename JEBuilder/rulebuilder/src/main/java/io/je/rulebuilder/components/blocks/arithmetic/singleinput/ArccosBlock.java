package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;

public class ArccosBlock extends SingleInputArithmeticBlock {

	public ArccosBlock(BlockModel blockModel) {
		super(blockModel);
	}

	private  ArccosBlock() {
		
	}
	
	

	
	
	@Override
	protected String getFormula() {
		return  "Math.acos( " +getInputRefName(0) + ")" ;
	}

	



}
