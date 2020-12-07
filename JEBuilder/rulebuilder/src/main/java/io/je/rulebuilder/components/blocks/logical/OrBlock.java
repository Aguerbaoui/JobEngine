package io.je.rulebuilder.components.blocks.logical;

import io.je.rulebuilder.components.blocks.GatewayBlock;
import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;

public class OrBlock extends GatewayBlock {

	public OrBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, boolean timePersistenceOn,
			int timePersistenceValue, TimePersistenceUnit timePersistenceUnit) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, timePersistenceOn, timePersistenceValue, timePersistenceUnit);
		// TODO Auto-generated constructor stub
	}

}
