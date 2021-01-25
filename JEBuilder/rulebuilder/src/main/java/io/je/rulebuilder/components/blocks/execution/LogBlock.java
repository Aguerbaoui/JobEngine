package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

public class LogBlock extends ExecutionBlock {
	
	String logMessage = "log output";

	public LogBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			logMessage = blockModel.getBlockConfiguration().getValue();
		}
	}
	
	 public LogBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		return "JELogger.info("+getClass()+",\" "+logMessage+"\")";
	}



}
