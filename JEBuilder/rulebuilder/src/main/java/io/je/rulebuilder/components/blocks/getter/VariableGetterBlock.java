package io.je.rulebuilder.components.blocks.getter;


import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.config.Keywords;
import io.je.rulebuilder.models.BlockModel;
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
            }

        } catch (Exception e) {
            isProperlyConfigured = false;
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
    public String getExpression() {
        return blockName.replaceAll("\\s+", "") + " : JEVariable ( jobEngineElementID == \"" + variableId + "\", " + getAttributeVariableName() + " : value )";
    }

    @Override
    public String getNotExpression() throws RuleBuildFailedException {
        // FIXME check if OK
        StringBuilder expression = new StringBuilder();

        expression.append("\n not ( " + getExpression() + " ) \n");

        return expression.toString();
    }

    @Override
    public String getReference(String optional) {
        return getBlockNameAsVariable() + ".getValue()";
    }

    public String asDouble(String val) {
        return "JEMathUtils.castToDouble(" + val + " )"; //" Double.valueOf( "+val+" )";
    }

    public String getAttributeVariableName() {
        return blockName.replaceAll("\\s+", "") + "Value";
    }

    @Override
    public String getAsOperandExpression() {
        return blockName.replaceAll("\\s+", "") + " : JEVariable ( jobEngineElementID == \"" + variableId + "\", " + getAttributeVariableName() + " : value,  " + Keywords.toBeReplaced + " )";
    }

}
