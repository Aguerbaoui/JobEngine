package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.BlockLinkModel;
import io.je.rulebuilder.components.blocks.logic.OrBlock;
import io.je.utilities.exceptions.RuleBuildFailedException;

import java.util.List;

public abstract class ConditionBlock extends Block{

	public ConditionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription,List<BlockLinkModel> inputBlockIds, List<BlockLinkModel> outputBlocksIds) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription,inputBlockIds,outputBlocksIds);
		
	}

	public ConditionBlock() {
		super();
	}

	public String getConsequences() throws RuleBuildFailedException {
		StringBuilder consequences = new StringBuilder();
		for(var block : getOutputBlocks())
		{
			if(block.getBlock() instanceof ExecutionBlock || block.getBlock() instanceof OrBlock)
			{
				consequences.append(block.getBlock().getExpression());
				consequences.append("\n");
			}
		}
		return consequences.toString();
	}



	
	
	

}
