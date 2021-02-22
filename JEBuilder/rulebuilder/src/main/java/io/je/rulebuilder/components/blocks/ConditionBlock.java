package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class ConditionBlock extends Block{

	public ConditionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription);
		
	}

	public ConditionBlock() {
		super();
	}

	public String getConsequences() throws RuleBuildFailedException {
		StringBuilder consequences = new StringBuilder();
		for(Block block : getOutputBlocks())
		{
			consequences.append(block.getExpression());
			consequences.append("\n");
		}
		return consequences.toString();
	}



	
	
	

}
