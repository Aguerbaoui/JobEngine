package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

public abstract class ArithmeticBlock extends LogicalBlock {

	public ArithmeticBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId,
			boolean timePersistenceOn, int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, timePersistenceOn, timePersistenceValue, timePersistenceUnit);
		// TODO Auto-generated constructor stub
	}
	
	

}
