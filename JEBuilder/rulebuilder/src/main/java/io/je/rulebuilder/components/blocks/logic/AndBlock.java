package io.je.rulebuilder.components.blocks.logic;

import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class AndBlock extends LogicBlock {

    public AndBlock(BlockModel blockModel) {
        super(blockModel);
    }

    public AndBlock() {

    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("(");
        for (int i = 0; i < inputBlocks.size(); i++) {
            expression.append("\n");

            expression.append(inputBlocks.get(i)
                    .getExpression());
            if (i != inputBlocks.size() - 1) expression.append(" and ");

        }
        expression.append(")");
        return expression.toString();
    }

    @Override
    public String getJoinExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        String joinId = inputBlocks.get(0)
                .getJoinId();

        expression.append(inputBlocks.get(0)
                .getJoinExpression());
        expression.append("\n");

        for (int i = 1; i < inputBlocks.size(); i++) {
            expression.append(inputBlocks.get(i)
                    .getJoinedExpression(joinId));
            expression.append("\n");

        }
        return expression.toString();

    }

    @Override
    public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append(inputBlocks.get(0)
                .getJoinedExpressionAsFirstOperand(joinId));
        expression.append("\n");
        for (int i = 1; i < inputBlocks.size(); i++) {
            expression.append(inputBlocks.get(i)
                    .getJoinedExpression(joinId));
            expression.append("\n");
        }
        return expression.toString();
    }


}
