package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.models.BlockModel;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public abstract class ComparisonBlock extends PersistableBlock {
	
	
	//if no second operand, the first operand is compared to a constant value (threshold)
	String value;
	
	
	public ComparisonBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getOperationId(), blockModel.getInputBlocksIds(),
				blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());
		if(blockModel.getBlockConfiguration()!=null && blockModel.getBlockConfiguration().getValue()!=null)
		{
			
			value = blockModel.getBlockConfiguration().getValue();
			
		}
	
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
	
	


}

