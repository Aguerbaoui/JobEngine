package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;
import io.je.rulebuilder.models.BlockModel;

public abstract class LogicBlock extends ConditionBlock {
	
	
	
	public LogicBlock(BlockModel blockModel) {
		super(blockModel.getJobEngineId(), blockModel.getProjectId(), blockModel.getRuleId(), Boolean.valueOf(blockModel.getTimePersistenceOn()), blockModel.getTimePersistenceValue(), blockModel.getTimePersistenceUnit());
		
	}

	
}
