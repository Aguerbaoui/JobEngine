package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;

import java.time.LocalDateTime;

public class JEVariable extends JEObject {

    private String variableName;

    private String variableTypeString;

    private Class<?> variableTypeClass;

    private Object variableValue;

    private JEType variableType;

    public String getVariableTypeString() {
        return variableTypeString;
    }

    public void setVariableTypeString(String variableTypeString) {
        this.variableTypeString = variableTypeString;
    }

    public Class<?> getVariableTypeClass() {
        return variableTypeClass;
    }

    public void setVariableTypeClass(Class<?> variableTypeClass) {
        this.variableTypeClass = variableTypeClass;
    }

    public Object getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(Object variableValue) {
        this.variableValue = variableValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public JEType getVariableType() {
        return variableType;
    }

    public void setVariableType(JEType variableType) {
        this.variableType = variableType;
    }

    public JEVariable() {}
    JEVariable(String projectId, String variableId, String variableName, JEType variableType, Object variableValue) {
        this.jobEngineProjectID = projectId;
        this.jobEngineElementID = variableId;
        this.variableName = variableName;
        this.variableType = variableType;
        this.variableValue = variableValue;
    }
    /*
     * returns the class type based on a string defining the type
     */
    public static Class<?> getType(String type) {
        type = type.toUpperCase();
        Class<?> classType = null;
        switch (type) {
            case "BYTE":
                classType = byte.class;
                break;
            case "SBYTE":
                classType = byte.class;
                break;
            case "INT":
                classType = int.class;
                break;
            case "SHORT":
                classType = short.class;
                break;
            case "LONG":
                classType = long.class;
                break;
            case "FLOAT":
                classType = float.class;
                break;
            case "DOUBLE":
                classType = double.class;
                break;
            case "CHAR":
                classType = char.class;
                break;
            case "BOOL":
                classType = boolean.class;
                break;
            case "OBJECT":
                classType = Object.class;
                break;
            case "STRING":
                classType = String.class;
                break;
            case "DATETIME":
                classType = LocalDateTime.class;
                break;
            case "VOID":
                classType = void.class;
                break;
            default:
                break; //add default value
        }
        return classType;
    }
}
