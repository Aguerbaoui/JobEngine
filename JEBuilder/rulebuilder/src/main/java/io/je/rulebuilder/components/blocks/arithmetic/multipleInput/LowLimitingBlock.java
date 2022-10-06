package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class LowLimitingBlock extends MultipleInputArithmeticBlock {

    String limit;

    public LowLimitingBlock(BlockModel blockModel) {
        super(blockModel);
        limit = String.valueOf(blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));

    }

    private LowLimitingBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        return "MathUtilities.lowLimiting( " + limit + ",";

    }

}
