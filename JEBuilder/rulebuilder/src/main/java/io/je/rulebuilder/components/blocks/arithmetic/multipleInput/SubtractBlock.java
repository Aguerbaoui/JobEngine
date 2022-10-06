package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class SubtractBlock extends MultipleInputArithmeticBlock {

    public SubtractBlock(BlockModel blockModel) {
        super(blockModel);
    }

    private SubtractBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        return "MathUtilities.subtract( ";

    }

}
