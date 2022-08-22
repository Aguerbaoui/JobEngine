package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import org.apache.commons.lang3.StringUtils;

import static io.je.rulebuilder.config.AttributesMapping.SOURCE_GETTER_ATTRIBUTE_NAME;
import static io.je.rulebuilder.config.AttributesMapping.SOURCE_LINKED_BLOCK_ID;

/*
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5004
 * source:DM/Variable
 * destination : Linked to getter
 */
public class AttachedSetterBlock extends ExecutionBlock {


    //SOURCE
    ValueType sourceType; //Static , Dynamic

    //static
    Object value;

    //variable
    String sourceVariableId;

    //DM
    String sourceInstanceId;
    String sourceAttributeName;
    Block linkedBlock;
    String sourceLinkedBlockId;
    String sourceGetterAttributeName;
    //DESTINATION
    String getterName;
    String destinationAttributeName;
    boolean ignoreWriteIfSameValue = true;
    //Constants
    String executionerMethod = "Executioner.writeToInstance(";

    public AttachedSetterBlock(BlockModel blockModel) {
        super(blockModel);

        try {
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration()
                    .get("ignoreWriteIfSameValue");
        } catch (Exception e) {
            JELogger.logException(e);
        }

        try {
            value = blockModel.getBlockConfiguration()
                    .get(AttributesMapping.NEWVALUE);
            sourceType = ValueType.valueOf((String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.SOURCE_VALUE_TYPE));
            destinationAttributeName = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.DESTINATION_ATTRIBUTE_NAME);
            sourceAttributeName = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.ATTRIBUTENAME);
            sourceGetterAttributeName = (String) blockModel.getBlockConfiguration()
                    .get(SOURCE_GETTER_ATTRIBUTE_NAME);
            sourceLinkedBlockId = (String) blockModel.getBlockConfiguration()
                    .get(SOURCE_LINKED_BLOCK_ID);
            sourceInstanceId = (String) blockModel.getBlockConfiguration()
                    .get("sourceInstance");
            sourceVariableId = (String) blockModel.getBlockConfiguration()
                    .get("sourceVariable");
            getterName = (String) blockModel.getBlockConfiguration()
                    .get(AttributesMapping.LINKED_GETTER_NAME);


            isProperlyConfigured = true;
            misConfigurationCause = "";
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = "AttachedSetterBlock : Exception occurred while initialize : " + e.getMessage();
            JELogger.logException(e);
        }


    }

    public AttachedSetterBlock() {
        super();
    }

    public Block getLinkedBlock() {
        return linkedBlock;
    }

    public void setLinkedBlock(Block linkedBlock) {
        this.linkedBlock = linkedBlock;
    }

    public String getSourceLinkedBlockId() {
        return sourceLinkedBlockId;
    }

    public void setSourceLinkedBlockId(String sourceLinkedBlockId) {
        this.sourceLinkedBlockId = sourceLinkedBlockId;
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {

        String getterInstanceId = getterName.replaceAll("\\s+", "") + ".getJobEngineElementID()";
        StringBuilder expression;
        try {
            switch (sourceType) {
                case STATIC:
                    expression = new StringBuilder();
                    expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.blockName + "\","
                            + getterInstanceId + ","
                            + "\"" + this.destinationAttributeName + "\","
                            + "\"" + this.value + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n");
                    expression.append("\n");

                    return expression.toString();
                case BLOCKS:
                    expression = new StringBuilder();
                    String getMethod = linkedBlock instanceof InstanceGetterBlock ? (".get" + StringUtils.capitalize(sourceGetterAttributeName)) + "()" : "";
                    String linkedBlockValue = linkedBlock.getReference("")
                            .replaceAll("\\s+", "") + getMethod;
                    expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.blockName + "\","
                            + getterInstanceId + ","
                            + "\"" + this.destinationAttributeName + "\","
                            + linkedBlockValue + ","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n");
                    expression.append("\n");

                    return expression.toString();
                case VARIABLE:
                    return "Executioner.updateInstanceAttributeValueFromVariable( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.blockName + "\","
                            + getterInstanceId + ","
                            + "\"" + this.destinationAttributeName + "\","
                            + "\"" + this.sourceVariableId + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n";
                case ATTRIBUTE:
                    expression = new StringBuilder();

                    expression.append("Executioner.updateInstanceAttributeValueFromAnotherInstance( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.blockName + "\","
                            + "\"" + this.sourceInstanceId + "\","
                            + "\"" + this.sourceAttributeName + "\","
                            + getterInstanceId + ","
                            + "\"" + this.destinationAttributeName + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n");
                    expression.append("\n");

                    return expression.toString();


                default:
                    throw new RuleBuildFailedException(JEMessages.INVALID_CONFIG);

            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = "AttachedSetterBlock : Exception occurred : " + e.getMessage();
            JELogger.logException(e);
            throw new RuleBuildFailedException(JEMessages.INVALID_CONFIG + " : " + misConfigurationCause);
        }

    }


}
