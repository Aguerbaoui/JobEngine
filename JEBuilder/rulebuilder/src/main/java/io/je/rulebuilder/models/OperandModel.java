package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.rulebuilder.components.enumerations.OperandDataType;
import io.je.rulebuilder.config.AttributesMapping;


/*
 * Operand model class
 */
public class OperandModel {

	  @JsonProperty(AttributesMapping.PROJECTID)
	  String projectId;

	  @JsonProperty(AttributesMapping.OPERANDID)
	  String operandId;

    @JsonProperty(AttributesMapping.OPERANDVALUE)
    String value;

    @JsonProperty(AttributesMapping.OPERANDTYPE)
    OperandDataType operandDataType;

    @JsonProperty(AttributesMapping.OPERANDCLASSNAME)
    String className;

    
    public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getOperandId() {
		return operandId;
	}

	public void setOperandId(String operandId) {
		this.operandId = operandId;
	}

	public OperandDataType getOperandDataType() {
		return operandDataType;
	}

	public void setOperandDataType(OperandDataType operandDataType) {
		this.operandDataType = operandDataType;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public OperandDataType getDataType() {
        return operandDataType;
    }

    public void setDataType(OperandDataType operandDataType) {
        this.operandDataType = operandDataType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "OperandModel [value=" + value + ", DataType=" + operandDataType + ", ClassName=" + className + "]";
    }


}
