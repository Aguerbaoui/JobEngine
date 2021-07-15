package io.je.rulebuilder.components.blocks.event;


import io.je.rulebuilder.components.blocks.PersistableBlock;

public  class TimeOutEventBlock extends PersistableBlock {
	
	String eventId = null;


	
	
	



	public TimeOutEventBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
			String blockDescription, int timePersistenceValue, String timePersistenceUnit, String eventId) {
		super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription, timePersistenceValue,
				timePersistenceUnit);
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
	public String getJoinExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpression(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpressionAsFirstOperand(String joindId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpression() {
		return " $e : JEEvent(jobEngineElementID ==\""+eventId+"\",isTriggered()==true)";
	}

	@Override
	public String getJoinExpressionAsFirstOperand() {
		// TODO Auto-generated method stub
		return null;
	}

}
