package io.je.rulebuilder.components.blocks;

public abstract class LogicalBlock extends Block {
	boolean timePersistenceOn;
	int timePersistenceValue;
	TimePersistenceUnit timePersistenceUnit;

}

enum TimePersistenceUnit
{
	SECOND,
	MINUTE,
	HOUR,
	DAY,
	MONTH,
	YEAR,
}