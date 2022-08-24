package io.je.rulebuilder.components.blocks.comparison;


import io.je.rulebuilder.components.BlockLinkModel;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
import io.je.rulebuilder.components.blocks.getter.VariableGetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.Operator;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

import java.util.List;
import java.util.Optional;

import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;


/**
 * Comparison Block is a class that represents the comparison elements in a rule.
 */
public class ComparisonBlock extends PersistableBlock {

    /*
     * comparison operator
     */
    protected String operator;



    /*
     * static operation threshold.
     * the threshold should be null if this block has more than 1 input
     * In the In/Out of Raneg blocks, this attributes holds the minimum value
     */

    String threshold = null;
    /*
     * In the In/Out of Raneg blocks, this attributes holds the maximum value
     */
    String maxRange = null;

    /*
     * In/Out Of Range parameter
     */
    boolean includeBounds = false;
    boolean formatToString = false;
    boolean isOperatorString = false;

    protected ComparisonBlock(String jobEngineElementID, String jobEngineProjectID, String ruleId, String blockName,
                              String blockDescription, int timePersistenceValue, String timePersistenceUnit, List<BlockLinkModel> inputBlockIds, List<BlockLinkModel> outputBlocksIds) {
        super(jobEngineElementID, jobEngineProjectID, ruleId, blockName, blockDescription, timePersistenceValue,
                timePersistenceUnit, inputBlockIds, outputBlocksIds);
    }

    public ComparisonBlock(BlockModel blockModel) {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
                blockModel.getDescription(),
                blockModel.getTimePersistenceValue(), blockModel.getTimePersistenceUnit(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());


        try {
            if (blockModel.getBlockConfiguration() != null) {

                if (blockModel.getBlockConfiguration()
                        .containsKey(AttributesMapping.VALUE)) {
                    threshold = String.valueOf(blockModel.getBlockConfiguration()
                            .get(AttributesMapping.VALUE));

                }
                if (blockModel.getBlockConfiguration()
                        .containsKey(AttributesMapping.VALUE2)) {
                    maxRange = String.valueOf(blockModel.getBlockConfiguration()
                            .get(AttributesMapping.VALUE2));

                }
                if (blockModel.getBlockConfiguration()
                        .containsKey(AttributesMapping.BOOLEANVALUE)) {
                    includeBounds = (Boolean) blockModel.getBlockConfiguration()
                            .get(AttributesMapping.BOOLEANVALUE);

                }

            }

            operator = getOperatorByOperationId(blockModel.getOperationId());
            //block is "not our of range" and "in range"
            formatToString = (blockModel.getOperationId() >= 2007 && blockModel.getOperationId() <= 2015) && inputBlockIds.size() == 1;
            isProperlyConfigured = true;
            misConfigurationCause = "";
            if (threshold == null && inputBlockIds.size() < 2) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.COMPARISON_BLOCK_UNABLE_TO_COMPARE_THRESHOLD_IS_NULL_AND_INPUT_BLOCKS_ID_CONTAINS_LESS_THAN_TWO_ELEMENTS;
            }
        } catch (Exception e) {
            LoggerUtils.logException(e);
            JELogger.error("Failed to build block : " + jobEngineElementName + " : " + e.getMessage(), LogCategory.DESIGN_MODE, jobEngineProjectID, LogSubModule.RULE, ruleId);
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.COMPARISON_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
        }

    }

    public ComparisonBlock() {
        super();
    }

    public String getOperatorByOperationId(int operationId) {
        Optional<Operator> operation = Operator.getOperatorByCode(operationId);
        isOperatorString = Operator.isStringOperator(operationId);
        if (operation
                .isPresent()) {
            return operation
                    .get()
                    .getFullName();
        } else return "";

    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable();
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {
        try {
            StringBuilder expression = new StringBuilder();

            checkBlockConfiguration();
            setParameters();

            // single input
            if (inputBlockLinks.size() == 1) {
                String inputExpression = getInputBlockByOrder(0).getAsOperandExpression()
                        .replaceAll(Keywords.toBeReplaced,
                                getOperationExpression());
                expression.append(inputExpression);
                //expression.append("\n" + " not(motor( jobEngineElementID == Getter1010.getJobEngineElementID() && temp < 10 &&  this startedby  Getter1010))");

                //in range / out of range blocks
            } else if (inputBlockLinks.size() == 3 || this instanceof InRangeBlock || this instanceof OutOfRangeBlock) {
                for (var input : inputBlockLinks) {
                    expression.append(input.getBlock()
                            .getExpression());
                    expression.append("\n");

                }
                // FIXME could reduce drools performance, check updated doc
                expression.append("eval(");
                expression.append(getOperationExpression());
                expression.append(")");

                //comparison blocks
            } else if (inputBlockLinks.size() == 2) {

                if (getInputBlockByOrder(0).equals(getInputBlockByOrder(1)) && getInputBlockByOrder(0) instanceof InstanceGetterBlock) {
                    expression.append(getInputBlockByOrder(0).getAsOperandExpression()
                            .replaceAll(Keywords.toBeReplaced,
                                    getOperationExpression()));
                } else {
                    if (getInputBlockByOrder(0).hasPrecedent(getInputBlockByOrder(1))) {
                        String firstOperand = getInputBlockByOrder(0).getAsOperandExpression()
                                .replaceAll(Keywords.toBeReplaced,
                                        getOperationExpression());
                        expression.append(firstOperand);

                    } else {
                        String firstOperand = getInputBlockByOrder(0).getExpression()
                                .replaceAll(Keywords.toBeReplaced,
                                        getOperationExpression());
                        expression.append(firstOperand);
                        String secondOperand = "";
                        expression.append("\n");
                        secondOperand = getInputBlockByOrder(1).getAsOperandExpression()
                                .replaceAll(Keywords.toBeReplaced,
                                        getOperationExpression());
                        expression.append(secondOperand);
                    }

                }

            }
            return expression.toString();
        } catch (Exception e) {
            LoggerUtils.logException(e);
            throw new RuleBuildFailedException(blockName + " is not configured properly");
        }
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME check if ok
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

    //check number of inputs
    protected void checkBlockConfiguration() throws RuleBuildFailedException {
        if (threshold == null && inputBlockIds.size() != 2) {
            throw new RuleBuildFailedException(blockName + " is not configured properly");
        }

    }

    protected void setParameters() {
        if (inputBlockIds.size() > 1) {
            threshold = getInputReferenceByOrder(1);
        }

    }

    protected String getOperationExpression() {
        if (isOperatorString) {
            String firstOperand = " ( (String) " + getInputReferenceByOrder(0) + " ) ";

            return firstOperand + getOperator() + " ( (String) " + formatOperator(threshold) + " ) ";
        }

        String firstOperand = (getInputBlockByOrder(0) instanceof VariableGetterBlock ? asDouble(getInputReferenceByOrder(0)) : getInputReferenceByOrder(0));
        return asDouble(firstOperand) + getOperator() + asDouble(formatOperator(threshold));

    }

    public String getOperator() {
        return operator;
    }

    public String formatOperator(String operator) {
        return formatToString ? "\"" + operator + "\"" : operator;
    }

    public String asDouble(String val) {
        return "JEMathUtils.castToDouble(" + val + " )"; //" Double.valueOf( "+val+" )";
    }

    protected String getMaxRange() {
        return maxRange;
    }

    protected void setMaxRange(String maxRange) {
        this.maxRange = maxRange;
    }

    protected boolean isIncludeBounds() {
        return includeBounds;
    }

    protected void setIncludeBounds(boolean includeBounds) {
        this.includeBounds = includeBounds;
    }

    @Override
    public String getAsOperandExpression() throws RuleBuildFailedException {
        throw new RuleBuildFailedException(this.blockName + " cannot be linked to comparison block");
    }

    @Override
    public String toString() {
        return "ComparisonBlock [threshold=" + threshold + ", timePersistenceValue=" + timePersistenceValue
                + ", timePersistenceUnit=" + timePersistenceUnit + ", ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
                + ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
    }


}

