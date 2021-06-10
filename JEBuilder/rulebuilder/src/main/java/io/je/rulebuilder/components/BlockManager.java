package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import io.je.rulebuilder.components.blocks.Block;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.logger.JELogger;

public class BlockManager {

	// key : block id, value : block
	ConcurrentHashMap<String, Block> blocks = new ConcurrentHashMap<>();




	
	/*
	 * add block
	 */
	public void addBlock(Block block)  {
		JELogger.debug(getClass(), block.toString());
		blocks.put(block.getJobEngineElementID(), block);
		

	}

	/*
	 * update block
	 */
	public void updateBlock(Block block)  {
		JELogger.info(getClass(), block.toString());
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
					JELogger.error("errrrrrrrrrrrrrrror");
					throw new RuleBuildFailedException(block.getBlockName() + " is not configured properly" );
				}
			}
		}
	}

	private void initBlock(Block block) {
		
		
		block.setInputBlocks(new ArrayList<>());
		block.setOutputBlocks(new ArrayList<>());

		for (String inputId : block.getInputBlockIds()) {
			block.addInput(blocks.get(inputId));
		}

		for (String outputId : block.getOutputBlockIds()) {
			block.addOutput(blocks.get(outputId));
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
