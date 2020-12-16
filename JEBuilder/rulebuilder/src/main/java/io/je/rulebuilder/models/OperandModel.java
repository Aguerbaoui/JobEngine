package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.components.enumerations.DataType;
import io.je.rulebuilder.config.BlockAttributesMapping;

public class OperandModel {


    @JsonProperty(BlockAttributesMapping.OPERANDVALUE)
    String value;

    @JsonProperty(BlockAttributesMapping.OPERANDTYPE)
    DataType DataType;

    @JsonProperty(BlockAttributesMapping.OPERANDCLASSNAME)
    String ClassName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public DataType getDataType() {
        return DataType;
    }

    public void setDataType(DataType dataType) {
        DataType = dataType;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    @Override
    public String toString() {
        return "OperandModel [value=" + value + ", DataType=" + DataType + ", ClassName=" + ClassName + "]";
    }


}
