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


	public PersistableBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription, int timePersistenceValue,
			TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription);
		this.timePersistenceValue = timePersistenceValue;
		this.timePersistenceUnit = timePersistenceUnit;
	}

	public PersistableBlock() {
		super();
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

	@Override
	public String toString() {
		return "Block [timePersistenceValue=" + timePersistenceValue + ", timePersistenceUnit="
				+ timePersistenceUnit + ", ruleId=" + ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks="
				+ outputBlocks + ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID="
				+ jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
}
