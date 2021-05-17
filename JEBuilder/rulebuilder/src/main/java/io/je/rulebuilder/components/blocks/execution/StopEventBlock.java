package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

public class StopEventBlock extends ExecutionBlock {
	
	String eventId = null;

	public StopEventBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			eventId = blockModel.getBlockConfiguration().getValue();
		}

	}
	
	 public StopEventBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		return "Executioner.stopEvent(\"" +jobEngineProjectID  +"\" , \""+ eventId  + "\");";
	}



}
