package io.je.rulebuilder.components.blocks.execution;

import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.rulebuilder.models.BlockModel;
import io.je.utilities.log.JELogger;

import java.util.List;

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
            isGeneric = (boolean) blockModel.getBlockConfiguration().get("isGeneric");
            classId = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSID);
            classPath = (String) blockModel.getBlockConfiguration().get(AttributesMapping.CLASSNAME);
            destinationAttributeName = (String) blockModel.getBlockConfiguration().get(AttributesMapping.ATTRIBUTENAME);
            instances = (List<String>) blockModel.getBlockConfiguration().get(AttributesMapping.SPECIFICINSTANCES);
            isProperlyConfigured = true;
            if (inputBlockIds.isEmpty()) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedSetterBlock : Input blocks ID empty";
            }
        } catch (Exception e) {
            isProperlyConfigured = false;
            misConfigurationCause = "LinkedSetterBlock : Exception occurred while initialize : " + e.getMessage();
            JELogger.logException(e);
        } finally {
            if (classId == null) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedSetterBlock : Class ID is null";
            } else if (classPath == null) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedSetterBlock : Class Path is null";
            } else if (destinationAttributeName == null) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedSetterBlock : Destination Attribute Name is null";
            } else if (instances == null) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedSetterBlock : Instances list null";
            } else if (instances.isEmpty()) {
                isProperlyConfigured = false;
                misConfigurationCause = "LinkedSetterBlock : Instances list empty";
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
