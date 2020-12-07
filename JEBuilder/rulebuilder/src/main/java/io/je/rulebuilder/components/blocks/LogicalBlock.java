package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

public abstract class LogicalBlock extends Block {
	
	

	boolean timePersistenceOn;
	int timePersistenceValue;
	TimePersistenceUnit timePersistenceUnit;
	
	public LogicalBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, boolean timePersistenceOn,
			int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId);
		this.timePersistenceOn = timePersistenceOn;
		this.timePersistenceValue = timePersistenceValue;
		this.timePersistenceUnit = timePersistenceUnit;
	}
	
	

}

