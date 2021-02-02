package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.utilities.constants.RuleBuilderErrors;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.ClassDefinition;

public class BlockManager {

	// key : block id, value : block
	HashMap<String, Block> blocks = new HashMap<>();

	List<ClassDefinition> topics = new ArrayList<>();



	
	/*
	 * add block
	 */
	public void addBlock(Block block) throws AddRuleBlockException {
		JELogger.info(getClass(), block.toString());
		blocks.put(block.getJobEngineElementID(), block);
		

	}

	/*
	 * update block
	 */
	public void updateBlock(Block block) throws AddRuleBlockException {
		JELogger.info(getClass(), block.toString());
		blocks.put(block.getJobEngineElementID(), block);
		

	}

	public void deleteBlock(String blockId) {
		//TODO: delete topic if getter block
		blocks.remove(blockId);


	}

	
	
	public void init() {
		if (!blocks.isEmpty()) {
			for (Block block : blocks.values()) {
				initBlock(block);
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


	public Set<Block> getRootBlocks() throws RuleBuildFailedException {
		Set<Block> roots = new HashSet<>();

		// number of execution blocks
		int executionBlockCounter = 0;
		// get root blocks
		for (Block ruleBlock : blocks.values()) {
			if (ruleBlock instanceof ExecutionBlock) {
				executionBlockCounter++;
				for (Block rootBlock : ruleBlock.getInputBlocks()) {
					if(rootBlock!=null)
					{
						roots.add( blocks.get(rootBlock.getJobEngineElementID()));
					}
					
				}
				
				// if exec block has no root, it's a root
				if(ruleBlock.getInputBlocks().isEmpty())
				{
					roots.add(ruleBlock);
				}

			}
		}
		// if this rule has no execution block, then it is not valid.
		if (executionBlockCounter == 0) {
			throw new RuleBuildFailedException(RuleBuilderErrors.NoExecutionBlock);
		}

		return roots;
	}

	public List<ClassDefinition> getTopics() {
		return topics;
	}


	
	
	
}
