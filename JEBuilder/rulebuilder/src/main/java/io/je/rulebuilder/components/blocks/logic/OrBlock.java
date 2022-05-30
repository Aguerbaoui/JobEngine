package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class OrBlock extends LogicBlock {


    public OrBlock(BlockModel blockModel) {
        super(blockModel);
        operator = "or";
    }

    public OrBlock() {

    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("(");
        for (int i = 0; i < inputBlocks.size(); i++) {
            expression.append("\n");

            expression.append(inputBlocks.get(i)
                    .getExpression());
            if (i != inputBlocks.size() - 1) expression.append(" or ");

        }
        expression.append(")");
        return expression.toString();
    }


    @Override
    public String getAsOperandExpression() {
        // not applicable for these blocks
        return null;
    }


    @Override
    public String getJoinExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        String joinId = inputBlocks.get(0)
                .getJoinId();
        expression.append("\n");
        expression.append("(");
        expression.append(inputBlocks.get(0)
                .getJoinExpression());
        expression.append(")");
        expression.append("\n");

        for (int i = 1; i < inputBlocks.size(); i++) {
            expression.append(operator);
            expression.append("\n");
            expression.append("(");
            expression.append(inputBlocks.get(i)
                    .getJoinedExpression(joinId));
            expression.append(")");
        }
        return expression.toString();

    }

    @Override
    public String getJoinedExpression(String joinId) throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append("(");
        expression.append(inputBlocks.get(0)
                .getJoinedExpressionAsFirstOperand(joinId));
        expression.append(")");
        expression.append("\n");

        for (int i = 1; i < inputBlocks.size(); i++) {
            expression.append(operator);
            expression.append("\n");
            expression.append("(");
            expression.append(inputBlocks.get(i)
                    .getJoinedExpression(joinId));
            expression.append(")");
        }
        return expression.toString();
    }


}
