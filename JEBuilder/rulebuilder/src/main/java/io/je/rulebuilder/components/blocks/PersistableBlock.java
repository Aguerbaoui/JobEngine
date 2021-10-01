package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

/*
 * blocks that can be persisted in time
 */
public abstract class PersistableBlock extends ConditionBlock {

	// persistence in time

	protected int timePersistenceValue;
	protected TimePersistenceUnit timePersistenceUnit;


	public PersistableBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription, int timePersistenceValue,
			String timePersistenceUnit,List<String> inputBlockIds, List<String> outputBlocksIds) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription,inputBlockIds,outputBlocksIds);
		this.timePersistenceValue = timePersistenceValue;
		switch(timePersistenceUnit)
		{
		case "second":
			this.timePersistenceUnit = TimePersistenceUnit.second;
			break;
		case "minute":
			this.timePersistenceUnit = TimePersistenceUnit.minute;
			break;
		case "hour":
			this.timePersistenceUnit = TimePersistenceUnit.hour;
			break;
		default:
			this.timePersistenceUnit = TimePersistenceUnit.second;

		}
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

	public String getUnitAsString()
	{
		switch(timePersistenceUnit)
		{
		
		case hour:
			return "h";
		case minute:
			return "m";
		case second:
			return "s";
		default:
			return "s";
		
		}
	}
	
	public String getPersistanceExpression()
	{
		if(timePersistenceValue != 0 && timePersistenceUnit != null) {
			return String.valueOf(timePersistenceValue) + getUnitAsString();
		}
		return null;
	}
	
	@Override
	public String getAsOperandExpression() {
		return null;
	}
	
	@Override
	public String toString() {
		return "Block [timePersistenceValue=" + timePersistenceValue + ", timePersistenceUnit="
				+ timePersistenceUnit + ", ruleId=" + ruleId + ", inputBlocks=" + inputBlocks + ", outputBlocks="
				+ outputBlocks + ", jobEngineElementID=" + jobEngineElementID + ", jobEngineProjectID="
				+ jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
}
