package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * this class handles the creation, update and deletion of blocks
 */
public  class BlockBuilder {

	

    private BlockBuilder() {
	}

	public static Block createBlock(BlockModel blockModel) {
    	if(blockModel == null || blockModel.getOperationId()==0)
    	{
    		//throw exception, can't add block 
    		return null;
    	}
    	int operation = blockModel.getOperationId()/1000;
    	switch(operation)
    	{
    	//Arithmetic block
    	case 1:
    		
    		break;
    	//Comparison block	
    	case 2:
    		break;
    	
    	default:
    		break;
    	}
		return null;
    }

       

}
