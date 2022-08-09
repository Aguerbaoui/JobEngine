package io.je.rulebuilder.components.blocks.logic;


import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class NotBlock extends LogicBlock {

    String operator;

    public NotBlock(BlockModel blockModel) {
        super(blockModel);
        operator = " not ";
    }

    public NotBlock() {
        // FIXME constant
        operator = " not ";
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append(operator + getNotExpression());

        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append("(");

        for (int i = 0; i < inputBlocks.size(); i++) {
            expression.append("\n");
            expression.append("(");
            expression.append(inputBlocks.get(i).getExpression());
            expression.append(")");
        }
        expression.append(")");

        return expression.toString();
    }


}
