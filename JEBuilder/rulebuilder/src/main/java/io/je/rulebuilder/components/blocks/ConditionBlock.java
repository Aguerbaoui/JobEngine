package io.je.rulebuilder.components.blocks;

import java.util.List;

public abstract class ConditionBlock extends Block{

	protected ConditionBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, int operationId,
			List<String> inputBlocks) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, operationId, inputBlocks);
	}
	
	

}
