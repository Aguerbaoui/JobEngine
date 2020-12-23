package io.je.rulebuilder.components.blocks;

import java.util.ArrayList;
import java.util.List;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;


/*
 * blocks that can be persisted in time
 */
public abstract class PersistableBlock extends ConditionBlock {

	
	// persistence in time
	  int timePersistenceValue;
	  TimePersistenceUnit timePersistenceUnit;
	  

	  
	protected PersistableBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, int operationId,
			List<String> inputBlocks, int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, operationId, inputBlocks);
		this.timePersistenceValue = timePersistenceValue;
		this.timePersistenceUnit = timePersistenceUnit;
	}
	public int getTimePersistenceValue() {
		return timePersistenceValue;
	}
	public void setTimePersistenceValue(int timePersistenceValue) {
		this.timePersistenceValue = timePersistenceValue;
	}
	public TimePersistenceUnit getTimePersistenceUnit() {
		return timePersistenceUnit;
	}
	public void setTimePersistenceUnit(TimePersistenceUnit timePersistenceUnit) {
		this.timePersistenceUnit = timePersistenceUnit;
	}




   
    
    


}

