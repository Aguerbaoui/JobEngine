package io.je.rulebuilder.components.blocks.logic;

import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class AndBlock extends LogicBlock {

    // FIXME constants
    public AndBlock(BlockModel blockModel) {
        super(blockModel);
        operator = " and ";
    }

    public AndBlock() {
        operator = " and ";
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < inputBlocks.size(); i++) {

            expression.append(inputBlocks.get(i).getExpression());

            if (i != inputBlocks.size() - 1) expression.append(operator);

        }
        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append(" not ( " + getExpression() + " ) ");
        return expression.toString();
    }

}
