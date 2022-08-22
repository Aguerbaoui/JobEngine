package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.log.JELogger;

/*
 * Block used to writing in a variable
 * source : previous block
 * operation id : 5007
 */
public class LinkedVariableSetterBlock extends ExecutionBlock {


    /*******************************Instance definition*******************************/
    String variableId;
    boolean ignoreWriteIfSameValue = true;


    public LinkedVariableSetterBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
        } catch (Exception e) {
            JELogger.logException(e);
        }
        try {
            variableId = (String) blockModel.getBlockConfiguration().get("variableId");

            isProperlyConfigured = true;
            misConfigurationCause = "";

            if (inputBlockIds.isEmpty()) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedVariableSetterBlock : Input blocks ID empty";
            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = "LinkedVariableSetterBlock : exception occurred while initialize : " + e.getMessage();
            JELogger.logException(e);
        }

    }

    public LinkedVariableSetterBlock() {
        super();
    }


    @Override
    public String getExpression() {
        return "Executioner.updateVariableValue( "
                + "\"" + this.jobEngineProjectID + "\","
                + "\"" + this.ruleId + "\","
                + "\"" + this.variableId + "\", "
                + inputBlockLinks.get(0).getReference()
                + ", " + "\"" + blockName + "\","
                + this.ignoreWriteIfSameValue
                + ");\r\n";


    }


}
