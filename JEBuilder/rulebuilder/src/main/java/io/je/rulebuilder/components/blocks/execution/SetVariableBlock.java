package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.log.JELogger;

/*
 * Block used to writing in an instance's attribute (from DM)
 * source : previous block
 * operation id : 5003
 */
public class SetVariableBlock extends ExecutionBlock {


    /*******************************Instance definition*******************************/
    String variableId;


    public SetVariableBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            variableId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.VALUE);
            isProperlyConfigured = true;
            misConfigurationCause = "";
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = "SetVariableBlock : exception occurred while initialize : " + e.getMessage();
            JELogger.logException(e);
        } finally {
            if (variableId == null) {
                isProperlyConfigured = false;
                misConfigurationCause = "SetVariableBlock : Variable Id null";
            }
        }

    }

    public SetVariableBlock() {
        super();
    }


    @Override
    public String getExpression() {
        return "Executioner.updateVariable(" + variableId + ", " + inputBlockLinks.get(0).getReference() + ");";
    }


}
