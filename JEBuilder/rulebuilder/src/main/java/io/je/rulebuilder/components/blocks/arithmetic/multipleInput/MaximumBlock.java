package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class MaximumBlock extends MultipleInputArithmeticBlock {

    public MaximumBlock(BlockModel blockModel) {
        super(blockModel);
        // TODO Auto-generated constructor stub
    }

    private MaximumBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, String type) {
        return "MathUtilities.maximum( ";

    }

}
