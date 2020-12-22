package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.components.enumerations.TimePersistenceUnit;
import io.je.rulebuilder.models.BlockModel;

/*
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public abstract class ComparisonBlock extends PersistableBlock {

	public ComparisonBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), 
				blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds(),blockModel.getTimePersistenceValue(),blockModel.getTimePersistenceUnit());
	}

	

}

