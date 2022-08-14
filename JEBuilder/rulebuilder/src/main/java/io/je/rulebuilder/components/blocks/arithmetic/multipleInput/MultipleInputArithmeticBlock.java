package io.je.rulebuilder.components.blocks.arithmetic.multipleInput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class MultipleInputArithmeticBlock extends ArithmeticBlock {


    public MultipleInputArithmeticBlock(BlockModel blockModel) {
        super(blockModel);
    }


    public MultipleInputArithmeticBlock() {

    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable();
    }


    @Override
    public String getExpression() throws RuleBuildFailedException {
        if (!alreadyScripted) {
            StringBuilder expression = generateAllPreviousBlocksExpressions();
            expression.append(generateBlockExpression(false));
            alreadyScripted = true;
            return expression.toString();

        }
        return "";
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME change getExpression if persistence added in the UI (replace \n with and), ...
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

    @Override
    public String getAsOperandExpression() throws RuleBuildFailedException {
        StringBuilder expression = generateAllPreviousBlocksExpressions();
        expression.append(generateBlockExpression(true));

        return expression.toString();
    }


    private StringBuilder generateAllPreviousBlocksExpressions() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        for (int i = 0; i < inputBlocks.size(); i++) {
            expression.append(inputBlocks.get(i).getBlock().getExpression());
            expression.append("\n");

        }
        return expression;
    }

    private String generateBlockExpression(boolean comparable) {
        String comparableExpression = " : Number() from ";
        if (comparable) {
            comparableExpression = " : Number(" + Keywords.toBeReplaced + ") from ";
        }
        StringBuilder expression = new StringBuilder();

        expression.append(getBlockNameAsVariable() + comparableExpression);
        expression.append(getArithmeticFormula(0, "number") + asDouble(inputBlocks.get(0).getReference()));
        for (int i = 1; i < inputBlocks.size(); i++) {
            expression.append(" , " + asDouble(inputBlocks.get(i).getReference()));
        }
        expression.append(")");
        if (stopExecutionIfInvalidInput) {
            expression.append("\n" + evaluateExecution(asDouble(inputBlocks.get(0).getReference())));
        }
        return expression.toString();

    }


}
