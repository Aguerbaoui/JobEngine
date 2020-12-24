package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public abstract class ComparisonBlock extends PersistableBlock {
	
	String threshold;

	public ComparisonBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), 
				blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds(),blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());
		if(blockModel.getInputBlocksIds().size()==1)
		{
			threshold = blockModel.getBlockConfiguration().getValue();
		}
	}
	
	public String getExpression()
	{
		return getOperator()+ threshold;
	}
	
	public abstract String getOperator();

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	@Override
	public String toString() {
		return "ComparisonBlock [threshold=" + threshold + ", timePersistenceValue=" + timePersistenceValue
				+ ", timePersistenceUnit=" + timePersistenceUnit + ", ruleId=" + ruleId + ", inputBlocks=" + inputBlocks
				+ ", outputBlocks=" + outputBlocks + ", jobEngineElementID=" + jobEngineElementID
				+ ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
	}
	
	



	

}

