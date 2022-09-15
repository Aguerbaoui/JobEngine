package io.je.rulebuilder.components.blocks;

import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public class LogicBlock extends PersistableBlock {

    protected String operator = "";

    public LogicBlock(BlockModel blockModel) {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(),
                blockModel.getBlockName(), blockModel.getDescription(), blockModel.getTimePersistenceValue(),
                blockModel.getTimePersistenceUnit(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());

        switch (blockModel.getOperationId()) {
            //and
            case 3001:
                operator = " and ";
                break;

            //or
            case 3002:
                operator = " or ";
                break;

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

        expression.append("\n not ( " + getExpression() + " ) \n");

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
