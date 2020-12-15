package io.je.rulebuilder.components.blocks;

import java.util.HashMap;
import java.util.Map;

/*
 * In memory repository for all blocks 
 */
public class BlockInventory {

	/*
	 * Map of all the blocks
	 */
	static Map<String, Block> blocks = new HashMap<>();

	/*
	 * add block to repository
	 */
	public static boolean addBlock(Block block) {
		if (!blockExists(block.getJobEngineElementID())) {
			return false;
		}
		blocks.put(block.getJobEngineElementID(), block);
		return true;
	}

	/*
	 * update block
	 */
	public boolean updateBlock(Block block) {

		blocks.put(block.getJobEngineElementID(), block);
		return true;

	}

	
	/*
	 * delete block
	 */
	public boolean deleteBlock(String blockId) {
		if (!blockExists(blockId)) {
			//block not found
			return false;
		}
		blocks.remove(blockId);
		return true;
	}

	private static boolean blockExists(String blockId) {

		return blocks.containsKey(blockId);
	}

}
