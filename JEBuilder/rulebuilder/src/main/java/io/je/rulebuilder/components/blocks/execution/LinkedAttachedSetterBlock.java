package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;

import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5008
 * source:input block
 * destination : Linked to getter
 */
public class LinkedAttachedSetterBlock extends ExecutionBlock {

    //SOURCE
    ValueType sourceType; //Static , Dynamic

    //static
    Object value;


    //DESTINATION
    String getterName;
    String destinationAttributeName;

    boolean ignoreWriteIfSameValue = true;
    //Constants
    String executionerMethod = "Executioner.writeToInstance(";


    public LinkedAttachedSetterBlock(BlockModel blockModel) {
        super(blockModel);

        try {
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
        } catch (Exception e) {
            JELogger.logException(e);
        }
        try {
            value = blockModel.getBlockConfiguration().get(AttributesMapping.NEWVALUE);
            destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.DESTINATION_ATTRIBUTE_NAME);
            getterName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.LINKED_GETTER_NAME);
            isProperlyConfigured = true;
            if (inputBlockIds.size() != 1) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_ATTACHED_SETTER_BLOCK_INPUT_BLOCKS_ID_SIZE_NOT_EQUAL_1;
            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.LINKED_ATTACHED_SETTER_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
            JELogger.logException(e);
        }


    }

    public LinkedAttachedSetterBlock() {
        super();
    }


    @Override
    public String getExpression() {
        StringBuilder expression = new StringBuilder();
        String getterInstanceId = getterName.replaceAll("\\s+", "") + ".getJobEngineElementID()";
        expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
                + "\"" + this.jobEngineProjectID + "\","
                + "\"" + this.ruleId + "\","
                + "\"" + this.blockName + "\","
                + getterInstanceId + ","
                + "\"" + this.destinationAttributeName + "\","
                + inputBlockLinks.get(0).getReference() + ","
                + this.ignoreWriteIfSameValue
                + ");\r\n");
        expression.append("\n");


        return expression.toString();

    }

}
