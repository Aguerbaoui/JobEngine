package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class ConditionBlock extends Block{

	public ConditionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription,List<String> inputBlockIds, List<String> outputBlocksIds) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription,inputBlockIds,outputBlocksIds);
		
	}

	public ConditionBlock() {
		super();
	}

	public String getConsequences() throws RuleBuildFailedException {
		StringBuilder consequences = new StringBuilder();
		for(Block block : getOutputBlocks())
		{
			if(block instanceof ExecutionBlock)
			{
				consequences.append(block.getExpression());
				consequences.append("\n");
			}
		}
		return consequences.toString();
	}



	
	
	

}
