package io.je.rulebuilder.components.blocks.logic;

import io.je.rulebuilder.components.blocks.LogicBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

import static io.je.rulebuilder.builder.RuleBuilder.*;

public class AndBlock extends LogicBlock {


    public AndBlock(BlockModel blockModel) {
        super(blockModel);
    }

    public AndBlock() {
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < inputBlockLinks.size(); i++) {

            expression.append(inputBlockLinks.get(i).getExpression());

            if (i != inputBlockLinks.size() - 1) expression.append(AND_DROOLS_CONDITION);

        }
        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();

        expression.append(NOT_DROOLS_PREFIX_CONDITION

                + getExpression()

                + NOT_DROOLS_SUFFIX_CONDITION);

        return expression.toString();
    }

}
