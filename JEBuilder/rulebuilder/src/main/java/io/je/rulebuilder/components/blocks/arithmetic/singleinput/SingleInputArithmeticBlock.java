package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {

    protected String defaultType = "number";

    public SingleInputArithmeticBlock(BlockModel blockModel) {
        super(blockModel);
        if (inputBlockIds.isEmpty()) {
            isProperlyConfigured = false;

        }
    }

    protected SingleInputArithmeticBlock() {
    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable();
    }

    protected abstract String getFormula();


    @Override
    protected String getArithmeticFormula(int level, String type) {
        if (type.equalsIgnoreCase("date")) {
            return "Date() from " + getFormula();
        } else if (type.equalsIgnoreCase("string")) {
            switch (level) {
                case 0:
                    return " String() from " + getFormula();
                case 1:
                    return " String(" + Keywords.toBeReplaced + ") from " + getFormula();
                default:
                    return " String() from " + getFormula();

            }
        } else if (type.equalsIgnoreCase("int")) {
            switch (level) {
                case 0:
                    return " Integer() from " + getFormula();
                case 1:
                    return " Integer(" + Keywords.toBeReplaced + ") from " + getFormula();
                default:
                    return " Integer() from " + getFormula();

            }
        } else if (type.equalsIgnoreCase("float")) {
            switch (level) {
                case 0:
                    return " Float() from " + getFormula();
                case 1:
                    return " Float(" + Keywords.toBeReplaced + ") from " + getFormula();
                default:
                    return " Float() from " + getFormula();

            }
        } else {
            switch (level) {
                case 0:
                    return " Double() from " + getFormula();
                case 1:
                    return " Double(" + Keywords.toBeReplaced + ") from " + getFormula();
                default:
                    return " Double() from " + getFormula();

            }
        }

    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME change getExpression if persistence added in the UI (replace \n with and), ...
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append(inputBlocks.get(0).getExpression());
        expression.append("\n");
        //int x = includesOperation? 1:0;
        expression.append(getBlockNameAsVariable() + " : " + getArithmeticFormula(0, defaultType));
        if (stopExecutionIfInvalidInput) {
            expression.append("\n" + evaluateExecution(asDouble(inputBlocks.get(0).getReference())));
        }
        return expression.toString();
    }


    @Override
    public String getAsOperandExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append(inputBlocks.get(0).getExpression());
        expression.append("\n");

        expression.append(getBlockNameAsVariable() + " : " + getArithmeticFormula(1, defaultType));
        if (stopExecutionIfInvalidInput) {
            expression.append("\n" + evaluateExecution(asDouble(inputBlocks.get(0).getReference())));
        }
        return expression.toString();
    }


    public String getDefaultType() {
        return defaultType;
    }


    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

}
