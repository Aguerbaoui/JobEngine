package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

/*
 * blocks that can be persisted in time
 */
public abstract class PersistableBlock extends ConditionBlock {

	// persistence in time

	int timePersistenceValue;
	TimePersistenceUnit timePersistenceUnit;


	public PersistableBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId,
			List<String> inputBlocks, List<String> outputBlocks, int timePersistenceValue,
			TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, inputBlocks, outputBlocks);
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
