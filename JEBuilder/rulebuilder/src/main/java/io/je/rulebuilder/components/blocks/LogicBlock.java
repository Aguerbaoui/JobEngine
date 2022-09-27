package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

import static io.je.rulebuilder.builder.RuleBuilder.NOT_DROOLS_PREFIX_CONDITION;
import static io.je.rulebuilder.builder.RuleBuilder.NOT_DROOLS_SUFFIX_CONDITION;

public class LogicBlock extends PersistableBlock {

    // FIXME is it well used. Check RuleBuilder constants.
    protected String operator = "";

    public LogicBlock(BlockModel blockModel) {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(),
                blockModel.getBlockName(), blockModel.getDescription(), blockModel.getTimePersistenceValue(),
                blockModel.getTimePersistenceUnit(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());

        switch (blockModel.getOperationId()) {
            // and
            case 3001:
                operator = " and ";
                break;

            // or
            case 3002:
                operator = " or ";
                break;

            // TODO not
        }
    }

    public LogicBlock() {

    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append("(");
        expression.append(inputBlockLinks.get(0).getExpression());
        expression.append(")");
        expression.append("\n");

        for (int i = 1; i < inputBlockLinks.size(); i++) {
            expression.append(operator);
            expression.append("\n");
            expression.append("(");
            expression.append(inputBlockLinks.get(i).getExpression());
            expression.append(")");
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

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable();
    }

    @Override
    public String getAsOperandExpression() {
        // not applicable for these blocks
        return null;
    }


}
