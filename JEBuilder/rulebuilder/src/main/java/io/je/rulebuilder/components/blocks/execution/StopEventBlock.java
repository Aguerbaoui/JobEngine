package io.je.rulebuilder.components.blocks.execution;

import java.util.List;

import io.je.rulebuilder.components.blocks.ExecutionBlock;

public class StopEventBlock extends ExecutionBlock {
	
	String eventId = null;


	public StopEventBlock() {
		// TODO Auto-generated constructor stub
	}




	public StopEventBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription, String eventId,List<String> inputBlockIds, List<String> outputBlocksIds) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription,inputBlockIds,outputBlocksIds);
		this.eventId = eventId;
	}




	@Override
	public String getExpression() {
		
		return "Executioner.untriggerEvent(\""+jobEngineProjectID+"\",$e.getJobEngineElementID());"
				+ "\n delete($e);";
	}


}
