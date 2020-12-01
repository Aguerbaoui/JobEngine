package io.je.rulebuilder.components.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockInventory {
	static Map<String,Map<String,Block>> blocks = new HashMap<>();
	
	public boolean addBlock(Block block)
	{
		return false;
	}
	
	public boolean updateBlock(Block block)
	{
		return false;
	}
	
	public boolean deleteBlock(String projectId,String blockId)
	{
		return false;
	}
	
	private boolean blockExists(String projectId,String blockId)
	{
		 Map projectBlocks = blocks.get(projectId);
		 return projectBlocks.containsKey(blockId);
	}	
	
	
}
