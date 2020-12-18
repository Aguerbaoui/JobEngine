package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.BlockInventory;
import io.je.rulebuilder.components.blocks.ComparisonBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;

/*
 * this class handles the creation, update and deletion of blocks
 */
public class BlockBuilder {


    public static Block createBlock(BlockModel blockModel) {
        Block block = null;
        String blockType = blockModel.getBlockType();
        switch (blockType) {
            case AttributesMapping.COMPARISONBLOCK:
                block = buildComparisonBlock(blockModel);
                break;
            case AttributesMapping.ARITHMETICBLOCK:
                break;
            case AttributesMapping.GATEWAYBLOCK:
                break;
            case AttributesMapping.EXECUTIONBLOCK:
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


    private static ComparisonBlock buildComparisonBlock(BlockModel blockModel) {

        return null;
    }

}
