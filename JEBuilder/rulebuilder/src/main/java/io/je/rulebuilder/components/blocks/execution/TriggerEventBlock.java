package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;

public class TriggerEventBlock extends ExecutionBlock {
	
	String eventId = null;
	String eventName = null;

	public TriggerEventBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			eventId = blockModel.getBlockConfiguration().getValue();
			eventName = blockModel.getBlockConfiguration().getValue2();

		}

	}
	
	 public TriggerEventBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		//return "Executioner.triggerEvent(\"" +jobEngineProjectID  +"\" , \""+ eventId  + "\");";
		return "Executioner.triggerEvent(\"" +jobEngineProjectID  +"\" , \""
				+ eventId  + "\",\"" 
				+eventName+"\",\""+
				ruleId+"\",\""+
				blockName
				+ "\");";

	}



}
