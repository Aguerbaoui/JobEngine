package io.je.rulebuilder.components.blocks.event;


import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.models.BlockModel;

public  class AcceptEventBlock extends Block {
	
	String eventId = null;


	public AcceptEventBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription());
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			eventId = blockModel.getBlockConfiguration().getValue();
		}

	}

	public AcceptEventBlock() {
		super();
	}

	@Override
	public String toString() {
		return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	@Override
	public String getAsFirstOperandExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsSecondOperandExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJoinedExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpression() {
		return "$"+blockName+" : JEEvent ( jobEngineElementID == "+eventId +", isTriggered() )";
	}

}
