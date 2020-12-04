package io.je.rulebuilder.components.blocks.logical;

import io.je.rulebuilder.components.blocks.GatewayBlock;

public class AndBlock extends GatewayBlock {

	@Override
	public String getExpression() {
		return " and ";
	}

}
