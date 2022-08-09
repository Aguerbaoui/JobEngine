package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;

public class AverageBlock extends MultipleInputArithmeticBlock {

    public AverageBlock(BlockModel blockModel) {
        super(blockModel);
    }

    private AverageBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, String type) {
        return "MathUtilities.average( ";

    }


}
