package io.je.rulebuilder.components.blocks.event;


import java.util.List;

import io.je.rulebuilder.components.blocks.PersistableBlock;

public  class TimeOutEventBlock extends PersistableBlock {
	
	String eventId = null;


	
	
	



	public TimeOutEventBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription, int timePersistenceValue, String timePersistenceUnit, String eventId,List<String> inputBlockIds, List<String> outputBlocksIds) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription, timePersistenceValue,
				timePersistenceUnit,inputBlockIds,outputBlocksIds);
		this.eventId = eventId;
	}

	public TimeOutEventBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	@Override
	public String getAsOperandExpression() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getExpression() {
		return " $e : JEEvent(jobEngineElementID ==\""+eventId+"\",isTriggered()==true)";
	}


}
