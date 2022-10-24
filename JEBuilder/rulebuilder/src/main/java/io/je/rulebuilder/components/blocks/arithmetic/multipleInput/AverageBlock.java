package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class AverageBlock extends MultipleInputArithmeticBlock {

    public AverageBlock(BlockModel blockModel) {
        super(blockModel);
    }

    private AverageBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        return "MathUtilities.average( ";

    }


}
