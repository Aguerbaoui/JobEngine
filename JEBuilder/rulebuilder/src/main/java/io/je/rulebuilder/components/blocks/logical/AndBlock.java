package io.je.rulebuilder.components.blocks.logical;

import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;

public class AndBlock extends LogicBlock {


    private AndBlock(BlockModel blockModel) {
        super(blockModel);

    }

    @Override
    public String getExpression() {
        return " and ";
    }

}
