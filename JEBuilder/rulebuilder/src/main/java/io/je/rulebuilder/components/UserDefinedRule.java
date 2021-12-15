package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.utilities.log.JELogger;

/*
 * Rules defined graphically by the user.
 * One UserDefinedRule can be equivalents to multiple drl rules 
 * Each Job engine rule is defined by a root block ( a logic or comparison block that precedes and execution sequence)
 */
public class UserDefinedRule extends JERule {

	/*
	 * rule attributes
	 */
	RuleParameters ruleParameters;

	/*
	 * Map of all the blocks that define this rule
	 */
	BlockManager blocks = new BlockManager();

	/*
	 * * One UserDefinedRule can be equivalents to multiple drl rules List of all
	 * the subRules
	 */

	List<String> subRules = new ArrayList<String>();

	public UserDefinedRule() {
		ruleParameters = new RuleParameters();
	}

	

	/*
	 * add a block to this user defined rule
	 */
	public void addBlock(Block block) {
		blocks.addBlock(block);
		isBuilt = false;
	}

	/*
	 * update a block in this user defined rule
	 */
	public void updateBlock(Block block) {
		blocks.updateBlock(block);
		isBuilt = false;

	}
	

	
	//Topics are class ids or classId#instanceId for specific instances
	//if a block has not specific instances, it means it is to be applied on all instances 
	@Override
	public void loadTopics()
	{
		this.setTopics(new ConcurrentHashMap());
		for(Block block : blocks.getAll())
		{
			
			
			if(block instanceof AttributeGetterBlock)
			{
				AttributeGetterBlock b = (AttributeGetterBlock)block;
				if(b.getSpecificInstances().isEmpty())
				{
					addTopic(b.getClassId(),this.getTopics());
				}else
				{
					for (String instanceId : b.getSpecificInstances())
					{
						addTopic(b.getClassId()+"#"+instanceId,instanceTopics);
					}
				}
			}
		}
		
		//for specific instance topics, they are only added if there isn't already a generic topic that inludes their class
		for(String instanceTopic : instanceTopics.keySet())
		{
			if(!this.getTopics().containsKey(instanceTopic.split("#")[0])) {
				addTopic(instanceTopic,this.getTopics());
			}
		}
		System.out.println(">>>>>>>>>>>>>>>>>TOPICS ARE" + this.getTopics());
		
	}
	
	

	/*
	 * delete a block in this user defined rule
	 */
	public void deleteBlock(String blockId) {
		/*if (blocks.getBlock(blockId) instanceof AttributeGetterBlock) {
			AttributeGetterBlock getter = (AttributeGetterBlock) blocks.getBlock(blockId);
			removeTopic(getter.getClassId());
		}*/
		blocks.deleteBlock(blockId);
		isBuilt = false;

	}

	public RuleParameters getRuleParameters() {
		return ruleParameters;
	}

	public void setRuleParameters(RuleParameters ruleParameters) {
		this.ruleParameters = ruleParameters;
		isBuilt = false;

	}

	public BlockManager getBlocks() {
		return blocks;
	}

	public Enumeration<String> getAllBlockIds()
	{
		return blocks.getAllBlockIds();
	}
	
	public void setBlocks(BlockManager blocks) {
		this.blocks = blocks;
	}

	public List<String> getSubRules() {
		return subRules;
	}

	public void setSubRules(List<String> subRules) {
		this.subRules = subRules;
	}

	public boolean containsBlock(String blockId) {
		return blocks.containsBlock(blockId);
	}

}
