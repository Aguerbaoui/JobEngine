package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.execution.AttachedSetterBlock;
import io.je.rulebuilder.components.blocks.execution.SetterBlock;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {

    // key : block id, value : block
    ConcurrentHashMap<String, Block> blocks = new ConcurrentHashMap<>();


    /*
     * add block
     */
    public void addBlock(Block block) {
        JELogger.debug(block.toString(),
                LogCategory.DESIGN_MODE, block.getJobEngineProjectID(),
                LogSubModule.RULE, block.getJobEngineElementID());
        blocks.put(block.getJobEngineElementID(), block);

    }

    /*
     * update block
     */
    public void updateBlock(Block block) {
        JELogger.debug(block.toString(),
                LogCategory.DESIGN_MODE, block.getJobEngineProjectID(),
                LogSubModule.RULE, block.getJobEngineElementID());
        blocks.put(block.getJobEngineElementID(), block);

    }

    public void deleteBlock(String blockId) {

        blocks.remove(blockId);

    }


    public void init() throws RuleBuildFailedException {
        if (!blocks.isEmpty()) {
            for (Block block : blocks.values()) {
                if (block.isProperlyConfigured()) {
                    initBlock(block);
                } else {
                    // TODO externalize messages
                    String message = block.getBlockName() + JEMessages.THE_BLOCK_IS_NOT_CONFIGURED_PROPERLY + block.getMisConfigurationCause();

                    JELogger.error(message, LogCategory.DESIGN_MODE, block.getJobEngineProjectID(),
                            LogSubModule.RULE, block.getRuleId(), block.getBlockName());

                    throw new RuleBuildFailedException(message);
                }
            }
        }
    }

    private void initBlock(Block block) {

        block.setAlreadyScripted(false);
        block.setInputBlockLinks(new ArrayList<>());
        block.setOutputBlockLinks(new ArrayList<>());
        if (block instanceof AttachedSetterBlock) {
            if (((AttachedSetterBlock) block).getSourceLinkedBlockId() != null)
                ((AttachedSetterBlock) block).setLinkedBlock(blocks.get(((AttachedSetterBlock) block).getSourceLinkedBlockId()));
        }
        if (block instanceof SetterBlock) {
            if (((SetterBlock) block).getSourceLinkedBlockId() != null)
                ((SetterBlock) block).setLinkedBlock(blocks.get(((SetterBlock) block).getSourceLinkedBlockId()));
        }
        for (var inputId : block.getInputBlockIds()) {
            block.addInputLink(blocks.get(inputId.getBlockId()), inputId.getConnectionName(), inputId.getOrder());
        }

        for (var outputId : block.getOutputBlockIds()) {
            block.addOutputLink(blocks.get(outputId.getBlockId()), outputId.getConnectionName(), outputId.getOrder());
        }

    }

    public void resetAllBlocks() {

        for (Block block : blocks.values()) {
            block.setAlreadyScripted(false);
        }
    }


    public Block getBlock(String blockId) {
        return blocks.get(blockId);
    }

    public boolean containsBlock(String blockId) {
        return blocks.containsKey(blockId);
    }

    public Enumeration<String> getAllBlockIds() {
        return blocks.keys();
    }

    public List<Block> getAll() {
        return new ArrayList<Block>(blocks.values());
    }


}
