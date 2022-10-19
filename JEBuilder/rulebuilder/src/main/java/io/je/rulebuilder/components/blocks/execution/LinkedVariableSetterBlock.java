package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;

import static io.je.rulebuilder.config.Constants.IGNORE_WRITE_IF_SAME_VALUE_DEFAULT_VALUE;
import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;

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
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration().getOrDefault("ignoreWriteIfSameValue",
                    IGNORE_WRITE_IF_SAME_VALUE_DEFAULT_VALUE);
        } catch (Exception e) {
            JELogger.logException(e);
        }
        try {
            variableId = (String) blockModel.getBlockConfiguration().getOrDefault("variableId", "");

            isProperlyConfigured = true;
            misConfigurationCause = "";

            if (inputBlockIds.isEmpty()) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_VARIABLE_SETTER_BLOCK_INPUT_BLOCKS_ID_EMPTY;
            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.LINKED_VARIABLE_SETTER_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
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
