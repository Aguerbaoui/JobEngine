package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.components.blocks.getter.InstanceGetterBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.rulebuilder.models.ValueType;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.RuleBuildFailedException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static io.je.rulebuilder.config.AttributesMapping.SOURCE_GETTER_ATTRIBUTE_NAME;
import static io.je.rulebuilder.config.AttributesMapping.SOURCE_LINKED_BLOCK_ID;

/**
 * Block used to writing in an instance's attribute (from DM)
 * operation id : 5005
 * source:DM/Variable
 * destination : Data Model Instance
 */
public class SetterBlock extends ExecutionBlock {


    //SOURCE
    ValueType sourceType; //ATTRIBUTE/STATIC/VARIBLE

    //static
    Object value;

    //variable
    String sourceVariableId;

    //DM
    String sourceClassName;
    String sourceInstanceId;
    String sourceAttributeName;
    //SOURCE
    Block linkedBlock;
    String sourceLinkedBlockId;
    String sourceGetterAttributeName;
    ValueType destinationType; //ATTRIBUTE/VARIBLE
    //DESTINATION
    List<String> destinationInstancesId;
    String destinationAttributeName;
    String destinationAttributeType;
    String destinationClassName;
    String destinationClassId; //to be added
    //variable
    String destinationVariableId;
    //Constants
    String executionerMethod = "Executioner.writeToInstance(";
    boolean isGeneric;  //to be added
    boolean ignoreWriteIfSameValue = true;

    public SetterBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            isGeneric = (boolean) blockModel.getBlockConfiguration().get("isGeneric");
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {

            //source configuration

            //source type
            sourceType = ValueType.valueOf((String) blockModel.getBlockConfiguration().get("sourceValueType"));

            //if source data model
            sourceClassName = (String) blockModel.getBlockConfiguration().get("class_name");
            sourceAttributeName = (String) blockModel.getBlockConfiguration().get("attribute_name");
            sourceInstanceId = (String) blockModel.getBlockConfiguration().get("sourceInstance");
            sourceGetterAttributeName = (String) blockModel.getBlockConfiguration()
                    .get(SOURCE_GETTER_ATTRIBUTE_NAME);
            sourceLinkedBlockId = (String) blockModel.getBlockConfiguration()
                    .get(SOURCE_LINKED_BLOCK_ID);
            //if source variable
            sourceVariableId = (String) blockModel.getBlockConfiguration().get("sourceVariable");

            value = blockModel.getBlockConfiguration().get("newValue");
            //destination configuration

            destinationType = ValueType.valueOf((String) blockModel.getBlockConfiguration().get("destinationType"));


            destinationAttributeName = (String) blockModel.getBlockConfiguration().get("destinationAttributeName");

            if (blockModel.getBlockConfiguration().containsKey(AttributesMapping.SPECIFICINSTANCES)) {
                destinationInstancesId = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
            }

            destinationClassName = (String) blockModel.getBlockConfiguration().get("destinationClassName");
            destinationAttributeType = (String) blockModel.getBlockConfiguration().get("destinationAttributeType");
            destinationVariableId = (String) blockModel.getBlockConfiguration().get("destinationVariableId");
            destinationClassId = (String) blockModel.getBlockConfiguration().get("destinationClassId");

            isProperlyConfigured = true;
        } catch (Exception e) {
            isProperlyConfigured = false;

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
