package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
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
	public void addBlock(BlockModel blockModel) throws AddRuleBlockException {

		verifyBlockFormatIsValid(blockModel);

		Block block = BlockGenerator.createBlock(blockModel);
		if (block == null) {
			throw new AddRuleBlockException(RuleBuilderErrors.AddRuleBlockFailed + " : " + blockModel.getBlockName());
		}
		block.setInputBlockIds(blockModel.getInputBlocksIds());
		block.setOutputBlockIds(blockModel.getOutputBlocksIds());
		
		
		//retrieve topic names from getter blocks 
		if(blockModel.getOperationId()== 4002 && blockModel.getBlockConfiguration() == null & blockModel.getBlockConfiguration().getClassId()!=null)
		{
			topics.add(new ClassDefinition(blockModel.getBlockConfiguration().getWorkspaceId(), blockModel.getBlockConfiguration().getClassId()) );
		}
		JELogger.info(getClass(), block.toString());
		blocks.put(blockModel.getBlockId(), block);
		

	}

	/*
	 * update block
	 */
	public void updateBlock(BlockModel blockModel) throws AddRuleBlockException {
		if (!blocks.containsKey(blockModel.getBlockId())) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockNotFound);
		}
		verifyBlockFormatIsValid(blockModel);

		Block block = BlockGenerator.createBlock(blockModel);
		if (block == null) {
			throw new AddRuleBlockException(RuleBuilderErrors.FailedToUpdateBlock);
		}

		block.setInputBlockIds(blockModel.getInputBlocksIds());
		block.setOutputBlockIds(blockModel.getOutputBlocksIds());
		JELogger.info(getClass(), block.toString());
		blocks.put(blockModel.getBlockId(), block);

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
	
	public void verifyBlockFormatIsValid(BlockModel blockModel) throws AddRuleBlockException {
		// block Id can't be null
		if (blockModel == null || blockModel.getBlockId() == null || blockModel.getBlockId().isEmpty()) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockIdentifierIsEmpty);

		}

		if (blockModel.getBlockName() == null || blockModel.getBlockName().isEmpty()) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockNameIsEmpty);

		}
		// block operation id can't be empty
		if (blockModel.getOperationId() == 0) {
			throw new AddRuleBlockException(RuleBuilderErrors.BlockOperationIdUnknown);
		}

	}

	public Set<ConditionBlock> getRootBlocks() throws RuleBuildFailedException {
		Set<ConditionBlock> roots = new HashSet<>();

		// number of execution blocks
		int executionBlockCounter = 0;
		// get root blocks
		for (Block ruleBlock : blocks.values()) {
			if (ruleBlock instanceof ExecutionBlock) {
				executionBlockCounter++;
				for (Block rootBlock : ruleBlock.getInputBlocks()) {
					if(rootBlock!=null)
					{
						roots.add((ConditionBlock) blocks.get(rootBlock.getJobEngineElementID()));
					}
					
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
