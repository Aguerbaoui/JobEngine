package io.je.rulebuilder.components.blocks;

import java.util.HashMap;
import java.util.Map;

/*
 * In memory block repository corresponding to one rule
 */
public class BlockInventory {

	/*
	 * id of the rule that contains these blocks
	 */
	String ruleId;
    /*
     * Map of all the blocks 
     * key = block id
     * value = block
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

    private static boolean blockExists(String blockId) {

        return blocks.containsKey(blockId);
    }
    
    
    public static Block getBlock(String blockId)
    {
    	if(blockExists(blockId))
    	{
    		return blocks.get(blockId);
    	}
    	return null;
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

}
