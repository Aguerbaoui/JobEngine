package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

public abstract class GatewayBlock extends LogicalBlock {
	
	
	
	public GatewayBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, boolean timePersistenceOn,
			int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, timePersistenceOn, timePersistenceValue, timePersistenceUnit);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getExpression() {
		return null;
	}

}
