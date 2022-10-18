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

import java.util.List;

import static io.je.rulebuilder.config.AttributesMapping.SOURCE_GETTER_ATTRIBUTE_NAME;
import static io.je.rulebuilder.config.AttributesMapping.SOURCE_LINKED_BLOCK_ID;
import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;

/**
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5005
 * source:DM/Variable
 * destination : Data Model Instance
 */
public class SetterBlock extends ExecutionBlock {


    // SOURCE
    ValueType sourceType; //ATTRIBUTE/STATIC/VARIABLE

    // Static
    Object value;

    // Variable
    String sourceVariableId;

    // DM
    String sourceClassName;
    String sourceInstanceId;
    String sourceAttributeName;
    // SOURCE
    Block linkedBlock;
    String sourceLinkedBlockId;
    String sourceGetterAttributeName;
    // ATTRIBUTE/VARIABLE
    ValueType destinationType;
    // DESTINATION
    List<String> destinationInstancesId;
    String destinationAttributeName;
    String destinationAttributeType;
    String destinationClassName;
    String destinationClassId; //to be added
    //variable
    String destinationVariableId;
    //Constants
    String executionerMethod = "Executioner.writeToInstance(";
    boolean isGeneric;  //TODO to be added
    boolean ignoreWriteIfSameValue = true;

    public SetterBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            isGeneric = (boolean) blockModel.getBlockConfiguration().getOrDefault("isGeneric", false); // FIXME not sent for Variable
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration().getOrDefault("ignoreWriteIfSameValue", true);
        } catch (Exception e) {
            JELogger.logException(e);
        }
        try {

            //source configuration

            //source type
            sourceType = ValueType.valueOf((String) blockModel.getBlockConfiguration().getOrDefault("sourceValueType", ""));

            //if source data model
            sourceClassName = (String) blockModel.getBlockConfiguration().getOrDefault("class_name", "");
            sourceAttributeName = (String) blockModel.getBlockConfiguration().getOrDefault("attribute_name", "");
            sourceInstanceId = (String) blockModel.getBlockConfiguration().getOrDefault("sourceInstance", "");
            sourceGetterAttributeName = (String) blockModel.getBlockConfiguration()
                    .getOrDefault(SOURCE_GETTER_ATTRIBUTE_NAME, "");
            sourceLinkedBlockId = (String) blockModel.getBlockConfiguration()
                    .getOrDefault(SOURCE_LINKED_BLOCK_ID, "");
            //if source variable
            sourceVariableId = (String) blockModel.getBlockConfiguration().getOrDefault("sourceVariable", "");

            value = blockModel.getBlockConfiguration().getOrDefault("newValue", "");
            //destination configuration

            destinationType = ValueType.valueOf((String) blockModel.getBlockConfiguration().getOrDefault("destinationType", ""));

            destinationAttributeName = (String) blockModel.getBlockConfiguration().getOrDefault("destinationAttributeName", "");

            if (blockModel.getBlockConfiguration().containsKey(AttributesMapping.SPECIFICINSTANCES)) {
                destinationInstancesId = (List<String>) blockModel.getBlockConfiguration().getOrDefault(AttributesMapping.SPECIFICINSTANCES, "");
            }

            destinationClassName = (String) blockModel.getBlockConfiguration().getOrDefault("destinationClassName", "");
            destinationAttributeType = (String) blockModel.getBlockConfiguration().getOrDefault("destinationAttributeType", "");
            destinationVariableId = (String) blockModel.getBlockConfiguration().getOrDefault("destinationVariableId", "");
            destinationClassId = (String) blockModel.getBlockConfiguration().getOrDefault("destinationClassId", "");

            isProperlyConfigured = true;
            misConfigurationCause = "";
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.SETTER_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
            JELogger.logException(e);
        }

    }

    public SetterBlock() {
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

    public String getSourceGetterAttributeName() {
        return sourceGetterAttributeName;
    }

    public void setSourceGetterAttributeName(String sourceGetterAttributeName) {
        this.sourceGetterAttributeName = sourceGetterAttributeName;
    }

    @Override
    public String getExpression() throws RuleBuildFailedException {

        StringBuilder expression;

        if (destinationType.equals(ValueType.ATTRIBUTE)) {

            switch (sourceType) {
                case STATIC:
                    expression = new StringBuilder();
                    for (String instanceId : destinationInstancesId) {
                        expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
                                + "\"" + this.jobEngineProjectID + "\","
                                + "\"" + this.ruleId + "\","
                                + "\"" + this.blockName + "\","
                                + "\"" + instanceId + "\","
                                + "\"" + this.destinationAttributeName + "\","
                                + "\"" + this.value + "\","
                                + this.ignoreWriteIfSameValue
                                + ");\r\n");
                        expression.append("\n");
                    }
                    return expression.toString();
                case BLOCKS:
                    expression = new StringBuilder();
                    String getMethod = linkedBlock instanceof InstanceGetterBlock ? (".get" + StringUtils.capitalize(sourceGetterAttributeName)) + "()" : "";
                    String linkedBlockValue = linkedBlock.getReference("")
                            .replaceAll("\\s+", "") + getMethod;
                    for (String instanceId : destinationInstancesId) {
                        expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
                                + "\"" + this.jobEngineProjectID + "\","
                                + "\"" + this.ruleId + "\","
                                + "\"" + this.blockName + "\","
                                + "\"" + instanceId + "\","
                                + "\"" + this.destinationAttributeName + "\","
                                + linkedBlockValue + ","
                                + this.ignoreWriteIfSameValue
                                + ");\r\n");
                        expression.append("\n");
                    }
                    return expression.toString();
                case VARIABLE:
                    expression = new StringBuilder();
                    for (String instanceId : destinationInstancesId) {
                        expression.append("Executioner.updateInstanceAttributeValueFromVariable( "
                                + "\"" + this.jobEngineProjectID + "\","
                                + "\"" + this.ruleId + "\","
                                + "\"" + this.blockName + "\","
                                + "\"" + instanceId + "\","
                                + "\"" + this.destinationAttributeName + "\","
                                + "\"" + this.sourceVariableId + "\","
                                + this.ignoreWriteIfSameValue
                                + ");\r\n");
                        expression.append("\n");
                    }
                    return expression.toString();

                case ATTRIBUTE:
                    expression = new StringBuilder();
                    for (String instanceId : destinationInstancesId) {
                        expression.append("Executioner.updateInstanceAttributeValueFromAnotherInstance( "
                                + "\"" + this.jobEngineProjectID + "\","
                                + "\"" + this.ruleId + "\","
                                + "\"" + this.blockName + "\","
                                + "\"" + this.sourceInstanceId + "\","
                                + "\"" + this.sourceAttributeName + "\","
                                + "\"" + instanceId + "\","
                                + "\"" + this.destinationAttributeName + "\","
                                + this.ignoreWriteIfSameValue
                                + ");\r\n");
                        expression.append("\n");
                    }
                    return expression.toString();


                default:
                    throw new RuleBuildFailedException("INVALID CONFIGURATION");

            }


        } else if (destinationType.equals(ValueType.VARIABLE)) {

            switch (sourceType) {
                case STATIC:
                    return "Executioner.updateVariableValue( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.destinationVariableId + "\","
                            + "\"" + this.value + "\"" + ", "
                            + "\"" + this.blockName + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n";
                case VARIABLE:
                    return "Executioner.updateVariableValueFromAnotherVariable( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.sourceVariableId + "\","
                            + "\"" + this.destinationVariableId + "\","
                            + "\"" + this.blockName + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n";
                case BLOCKS:
                    expression = new StringBuilder();
                    String getMethod = linkedBlock instanceof InstanceGetterBlock ? (".get" + StringUtils.capitalize(sourceGetterAttributeName)) + "()" : "";
                    String linkedBlockValue = linkedBlock.getReference("")
                            .replaceAll("\\s+", "") + getMethod;

                    expression.append("Executioner.updateVariableValue( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.destinationVariableId + "\","
                            + linkedBlockValue + ","
                            + "\"" + this.blockName + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n");
                    expression.append("\n");

                    return expression.toString();
                case ATTRIBUTE:
                    return "Executioner.updateVariableValueFromDataModel( "
                            + "\"" + this.jobEngineProjectID + "\","
                            + "\"" + this.ruleId + "\","
                            + "\"" + this.destinationVariableId + "\","
                            + "\"" + this.sourceInstanceId + "\","
                            + "\"" + this.sourceAttributeName + "\","
                            + "\"" + this.blockName + "\","
                            + this.ignoreWriteIfSameValue
                            + ");\r\n";

                default:
                    throw new RuleBuildFailedException(JEMessages.INVALID_CONFIG);

            }

        }
        return "";

    }

    public String getDestinationClassId() {
        return destinationClassId;
    }

    public void setDestinationClassId(String destinationClassId) {
        this.destinationClassId = destinationClassId;
    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public void setGeneric(boolean isGeneric) {
        this.isGeneric = isGeneric;
    }


}
