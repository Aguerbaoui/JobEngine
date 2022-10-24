package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;

import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;

/*
 * Block used to writing in an instance's attribute (from DM)
 * source : previous block
 * operation id : 5006 // FIXME not used currently (Setter called instead)
 */
public class VariableSetterBlock extends ExecutionBlock {


    /*******************************Instance definition*******************************/
    String variableId;


    public VariableSetterBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            variableId = (String) blockModel.getBlockConfiguration().getOrDefault(AttributesMapping.VALUE, "");
            isProperlyConfigured = true;
            misConfigurationCause = "";
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.SET_VARIABLE_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
            JELogger.logException(e);
        } finally {
            if (variableId == null) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.SET_VARIABLE_BLOCK_VARIABLE_ID_NULL;
            }
        }

    }

    public VariableSetterBlock() {
        super();
    }


    @Override
    public String getExpression() {
        return "Executioner.updateVariable(" + variableId + ", " + inputBlockLinks.get(0).getReference() + ");";
    }


}
