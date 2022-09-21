package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class NotBlock extends LogicBlock {

    // TODO constants
    public NotBlock(BlockModel blockModel) {
        super(blockModel);
        operator = " not ";
    }

    public NotBlock() {
        operator = " not ";
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append(operator + " ( " + getNotExpression() + " ) ");

        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        for (int i = 0; i < inputBlockLinks.size(); i++) {

            expression.append(inputBlockLinks.get(i).getExpression());

            if (i != inputBlockLinks.size() - 1) expression.append(" and ");

        }

        return expression.toString();
    }


}
