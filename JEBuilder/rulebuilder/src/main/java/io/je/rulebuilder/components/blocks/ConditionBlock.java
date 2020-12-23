package io.je.rulebuilder.components.blocks;

import java.util.List;

public abstract class ConditionBlock extends Block{

	public ConditionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, 
			List<String> inputBlocks, List<String> outputBlocks) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, inputBlocks, outputBlocks);
	}
	
	

}
