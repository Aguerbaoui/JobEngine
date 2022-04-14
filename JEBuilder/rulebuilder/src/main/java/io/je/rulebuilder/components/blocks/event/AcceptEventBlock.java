package io.je.rulebuilder.components.blocks.event;


import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public  class AcceptEventBlock extends ConditionBlock {
	
	String eventId = null;


	public AcceptEventBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
				blockModel.getDescription(),blockModel.getInputBlocksIds(),blockModel.getOutputBlocksIds());
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().get(AttributesMapping.VALUE)!=null)
		{
			eventId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE);
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
	public String getExpression() {
		return "$"+blockName.replaceAll("\\s+", "")+" : JEEvent ( jobEngineElementID == \""+eventId +"\", isTriggered() )";
	}

	@Override
	public String getAsOperandExpression() throws RuleBuildFailedException {
		throw new RuleBuildFailedException(this.blockName+" cannot be linked to comparison block.");
	}


}
