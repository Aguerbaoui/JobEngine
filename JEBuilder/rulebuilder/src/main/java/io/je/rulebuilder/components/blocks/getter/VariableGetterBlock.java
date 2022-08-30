package io.je.rulebuilder.components.blocks.getter;


import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;
import utils.log.LoggerUtils;

/*
 * operation Id 4005
 */
public class VariableGetterBlock extends ConditionBlock {

    String variableId = null;


    public VariableGetterBlock(BlockModel blockModel) {
        super(blockModel.getBlockId(), blockModel.getProjectId(), blockModel.getRuleId(), blockModel.getBlockName(),
                blockModel.getDescription(), blockModel.getInputBlocksIds(), blockModel.getOutputBlocksIds());
        try {
            variableId = (String) blockModel.getBlockConfiguration()
                    .get("variableId");
            isProperlyConfigured = true;
            if (variableId == null) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.VARIABLE_GETTER_BLOCK_VARIABLE_ID_IS_NULL;
            }

        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.VARIABLE_GETTER_BLOCK_EXCEPTION_WHILE_LOADING_VARIABLE_ID + e.getMessage();

            LoggerUtils.logException(e);
        }

    }

    public VariableGetterBlock() {
        super();
    }

    @Override
    public String toString() {
        return "ExecutionBlock [ruleId=" + ruleId + ", jobEngineElementID=" + jobEngineElementID
                + ", jobEngineProjectID=" + jobEngineProjectID + ", jeObjectLastUpdate=" + jeObjectLastUpdate + "]";
    }


    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME check if OK
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

    public String getAttributeVariableName() {
        return blockName.replaceAll("\\s+", "") + "Value";
    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable() + ".getValue()";
    }

    @Override
    public String getExpression() {
        return blockName.replaceAll("\\s+", "") + " : JEVariable ( jobEngineElementID == \"" + variableId + "\"," + getAttributeVariableName() + " : value )";
    }

    @Override
    public String getAsOperandExpression() {
        return blockName.replaceAll("\\s+", "") + " : JEVariable ( jobEngineElementID == \"" + variableId + "\", " + getAttributeVariableName() + " : value,  " + Keywords.toBeReplaced + " )";
    }

}
