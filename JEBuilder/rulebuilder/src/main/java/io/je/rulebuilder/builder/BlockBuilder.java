package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.BlockInventory;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.config.BlockAttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * this class handles the creation, update and deletion of blocks
 */
public class BlockBuilder {


    public Block createBlock(BlockModel blockModel) {
        Block block = null;
        String blockType = blockModel.getBlockType();
        switch (blockType) {
            case BlockAttributesMapping.COMPARISONBLOCK:
                block = buildComparisonBlock(blockModel);
                break;
            case BlockAttributesMapping.ARITHMETICBLOCK:
                break;
            case BlockAttributesMapping.GATEWAYBLOCK:
                break;
            case BlockAttributesMapping.EXECUTIONBLOCK:
                break;

            default:
                break;


        }

        if (block != null) {
            BlockInventory.addBlock(block);

        }
        return null;
    }

    public Block updateBlock(Object jsonBlockInput) {
        return null;
    }

    public Block deleteBlock(Object blockId) {
        return null;
    }


    private ComparisonBlock buildComparisonBlock(BlockModel blockModel) {

        return null;
    }

}
