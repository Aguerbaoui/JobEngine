package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;

public class DivideBlock extends MultipleInputArithmeticBlock {

    public DivideBlock(BlockModel blockModel) {
        super(blockModel);
    }

    private DivideBlock() {
    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        return "MathUtilities.divide( ";

    }

    @Override
    protected String evaluateExecution(String... inputs) {
        return "eval(JEMathUtils.divisionByZero(\"" + this.jobEngineProjectID + "\",\"" + this.ruleId + "\",\"" + this.blockName + "\"," + inputs[0] + "))\n";
    }

}
