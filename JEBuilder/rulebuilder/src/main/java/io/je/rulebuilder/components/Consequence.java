package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.ExecutionBlock;

public class Consequence  {
	ExecutionBlock value;
	
	public String getExpression()
	{
		return "JELogger.info(\"test\" )";
	}

}
