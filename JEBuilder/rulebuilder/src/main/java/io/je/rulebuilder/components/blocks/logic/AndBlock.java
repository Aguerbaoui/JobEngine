package io.je.rulebuilder.components.blocks.logic;

import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class AndBlock extends LogicBlock {

    public AndBlock(BlockModel blockModel) {
        super(blockModel);
        operator = " and ";
    }

    public AndBlock() {
        // FIXME constant
        operator = " and ";
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append(" ( ");
        for (int i = 0; i < inputBlocks.size(); i++) {
            expression.append("\n");

            expression.append(inputBlocks.get(i)
                    .getExpression());
            if (i != inputBlocks.size() - 1) expression.append(operator);

        }
        expression.append(" ) ");
        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append(" not " + getExpression());
        return expression.toString();
    }

}
