package io.je.rulebuilder.components.blocks;

import java.util.List;

import io.je.rulebuilder.models.BlockModel;


/*
 * Blocks used to reference comparaison and arithmetic block's operands
 */
public abstract class GetterBlock extends Block {
	
	
		String classId;
	    
		String attributeName;

	    List<String> specificInstances;
		

	public GetterBlock(BlockModel blockModel) {
		super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getOperationId(),
				blockModel.getInputBlocksIds());

	}
}
