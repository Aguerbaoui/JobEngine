package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.EventException;

public class TriggerEventBlock extends ExecutionBlock {
	
	String eventId = null;

	public TriggerEventBlock(BlockModel blockModel) {
		super(blockModel);
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			eventId = blockModel.getBlockConfiguration().getValue();
		}

	}
	
	 public TriggerEventBlock() {
		 super();
	}

	@Override
	public String getExpression() {
		return "Executioner.triggerEvent(" +eventId  +" , "+ jobEngineElementID + ");";
	}



}
