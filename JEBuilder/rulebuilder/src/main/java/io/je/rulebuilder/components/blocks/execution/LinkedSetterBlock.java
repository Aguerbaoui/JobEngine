package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.log.JELogger;

import java.util.List;

import static io.je.utilities.constants.JEMessages.EXCEPTION_OCCURRED_WHILE_INITIALIZE;

/*
 * Block used to writing in an instance's attribute (from DM)
 * source : previous block
 * operation id : 5003
 */
public class LinkedSetterBlock extends ExecutionBlock {


    boolean isGeneric;

    /*******************************Instance definition*******************************/
    String classId;
    String classPath;
    String destinationAttributeName;
    String destinationAttributeType;

    List<String> instances;
    boolean ignoreWriteIfSameValue = true;

    public LinkedSetterBlock(BlockModel blockModel) {
        super(blockModel);
        try {
            ignoreWriteIfSameValue = (boolean) blockModel.getBlockConfiguration().get("ignoreWriteIfSameValue");
        } catch (Exception e) {
            JELogger.logException(e);
        }

        try {
            isGeneric = (boolean) blockModel.getBlockConfiguration().getOrDefault("isGeneric", null); // FIXME is sent?
            classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
            classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
            destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
            instances = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
            isProperlyConfigured = true;
            if (inputBlockIds.isEmpty()) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK_INPUT_BLOCKS_ID_EMPTY;
            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK + EXCEPTION_OCCURRED_WHILE_INITIALIZE + e.getMessage();
            JELogger.logException(e);
        } finally {
            if (classId == null) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK_CLASS_ID_IS_NULL;
            } else if (classPath == null) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK_CLASS_PATH_IS_NULL;
            } else if (destinationAttributeName == null) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK_DESTINATION_ATTRIBUTE_NAME_IS_NULL;
            } else if (instances == null) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK_INSTANCES_LIST_NULL;
            } else if (instances.isEmpty()) {
                isProperlyConfigured = false;
                misConfigurationCause = JEMessages.LINKED_SETTER_BLOCK_INSTANCES_LIST_EMPTY;
            }
        }

    }


    public LinkedSetterBlock() {
        super();
    }


    @Override
    public String getExpression() {
        StringBuilder expression = new StringBuilder();

        for (String instance : instances) {
            expression.append("Executioner.updateInstanceAttributeValueFromStaticValue( "
                    + "\"" + this.jobEngineProjectID + "\","
                    + "\"" + this.ruleId + "\","
                    + "\"" + this.blockName + "\","
                    + "\"" + instance + "\","
                    + "\"" + this.destinationAttributeName + "\","
                    + inputBlockLinks.get(0).getReference() + ","
                    + this.ignoreWriteIfSameValue


                    + ");\r\n");
            expression.append("\n");
        }


        return expression.toString();

    }

    public boolean isGeneric() {
        return isGeneric;
    }

    public void setGeneric(boolean isGeneric) {
        this.isGeneric = isGeneric;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }


}
