package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class MaximumBlock extends MultipleInputArithmeticBlock {

    public MaximumBlock(BlockModel blockModel) {
        super(blockModel);
        // TODO Auto-generated constructor stub
    }

    private MaximumBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        return "MathUtilities.maximum( ";

    }

}
