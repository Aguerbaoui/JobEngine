package io.je.rulebuilder.components.blocks.arithmetic.singleinput;

import io.je.rulebuilder.components.blocks.ArithmeticBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.beans.UnifiedType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;

public abstract class SingleInputArithmeticBlock extends ArithmeticBlock {

    protected UnifiedType defaultType = UnifiedType.DOUBLE;

    public SingleInputArithmeticBlock(BlockModel blockModel) {
        super(blockModel);
        if (inputBlockIds.isEmpty()) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.SINGLE_INPUT_ARITHMETIC_BLOCK_INPUT_BLOCKS_ID_EMPTY;
        }
    }

    protected SingleInputArithmeticBlock() {
    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable();
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append(inputBlockLinks.get(0).getExpression());
        expression.append("\n");
        //int x = includesOperation? 1:0;
        expression.append(getBlockNameAsVariable() + " : " + getArithmeticFormula(0, defaultType));
        if (stopExecutionIfInvalidInput) {
            expression.append("\n" + evaluateExecution(asDouble(inputBlockLinks.get(0).getReference())));
        }
        return expression.toString();
    }

    @Override
    public String getAsOperandExpression() throws RuleBuildFailedException {
        StringBuilder expression = new StringBuilder();
        expression.append("\n");
        expression.append(inputBlockLinks.get(0).getExpression());
        expression.append("\n");

        expression.append(getBlockNameAsVariable() + " : " + getArithmeticFormula(1, defaultType));
        if (stopExecutionIfInvalidInput) {
            expression.append("\n" + evaluateExecution(asDouble(inputBlockLinks.get(0).getReference())));
        }
        return expression.toString();
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME change getExpression if persistence added in the UI (replace \n with and), ...
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

    @Override
    protected String getArithmeticFormula(int level, UnifiedType type) {
        switch (type) {
            case SBYTE:
                if (level == 1) {
                    return " Byte(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Byte() from " + getFormula();
            case UINT16:
            case INT32:
            case INT:
                if (level == 1) {
                    return " Integer(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Integer() from " + getFormula();
            case BYTE:
            case INT16:
            case SHORT:
                if (level == 1) {
                    return " Short(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Short() from " + getFormula();
            case UINT32:
            case INT64:
            case LONG:
                if (level == 1) {
                    return " Long(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Long() from " + getFormula();
            case UINT64:
            case FLOAT:
            case SINGLE:
                if (level == 1) {
                    return " Float(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Float() from " + getFormula();
            case DOUBLE:
                if (level == 1) {
                    return " Double(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Double() from " + getFormula();

            case BOOL:
                if (level == 1) {
                    return " Boolean(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " Boolean() from " + getFormula();
            case OBJECT:
            case STRING:
                if (level == 1) {
                    return " String(" + Keywords.toBeReplaced + ") from " + getFormula();
                }
                return " String() from " + getFormula();
            case DATETIME:
                return "Date() from " + getFormula();

            default:
                break; //add default value
        }

        return " String() from " + getFormula();
    }

    protected abstract String getFormula();

    public UnifiedType getDefaultType() {
        return defaultType;
    }


    public void setDefaultType(UnifiedType defaultType) {
        this.defaultType = defaultType;
    }

}
