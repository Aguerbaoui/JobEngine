package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class HighLimitingBlock extends MultipleInputArithmeticBlock {

    String limit;

    public HighLimitingBlock(BlockModel blockModel) {
        super(blockModel);
        limit = String.valueOf(blockModel.getBlockConfiguration().get(AttributesMapping.VALUE));

    }


    private HighLimitingBlock() {

    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        return "MathUtilities.highLimiting( " + limit + ",";

    }

}
