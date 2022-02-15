package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.je.rulebuilder.components.blocks.Block;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

public class BlockManager {

	// key : block id, value : block
	ConcurrentHashMap<String, Block> blocks = new ConcurrentHashMap<>();




	
	/*
	 * add block
	 */
	public void addBlock(Block block)  {
		JELogger.debug(block.toString(),
				LogCategory.DESIGN_MODE, block.getJobEngineProjectID(),
				LogSubModule.RULE,block.getJobEngineElementID());
		blocks.put(block.getJobEngineElementID(), block);
		

	}

	/*
	 * update block
	 */
	public void updateBlock(Block block)  {
		JELogger.debug(block.toString(),
				LogCategory.DESIGN_MODE, block.getJobEngineProjectID(),
				LogSubModule.RULE,block.getJobEngineElementID());
		blocks.put(block.getJobEngineElementID(), block);
		

	}

	public void deleteBlock(String blockId) {
		
		blocks.remove(blockId);


	}


	
	public void init() throws RuleBuildFailedException {
		if (!blocks.isEmpty()) {
			for (Block block : blocks.values()) {
				if(block.isProperlyConfigured())
				{
					initBlock(block);
				}
				else
				{
					JELogger.error("Error with block configuration",
							LogCategory.DESIGN_MODE, block.getJobEngineProjectID(),
							LogSubModule.RULE,block.getJobEngineElementID());
					throw new RuleBuildFailedException(block.getBlockName() + " is not configured properly" );
				}
			}
		}
	}

	private void initBlock(Block block) {
		
		block.setAlreadyScripted(false);
		block.setInputBlocks(new ArrayList<>());
		block.setOutputBlocks(new ArrayList<>());

		for (String inputId : block.getInputBlockIds()) {
			block.addInput(blocks.get(inputId));
		}

		for (String outputId : block.getOutputBlockIds()) {
			block.addOutput(blocks.get(outputId));
		}
	}

	public void resetAllBlocks() {
		
		for(Block block : blocks.values())
		{
			block.setAlreadyScripted(false);
		}
	}



	public Block getBlock(String blockId)
	{
		return blocks.get(blockId);
	}

	public boolean containsBlock(String blockId) {
		return blocks.containsKey(blockId);
	}

	public Enumeration<String> getAllBlockIds() {
		return  blocks.keys();
	}
	
	public List<Block> getAll()
	{
		return new ArrayList<Block>(blocks.values()) ;
	}

	
	
	
}
