package io.je.utilities.beans;

import io.je.utilities.runtimeobject.JEObject;

public class JEVariable extends JEObject {

    private String variableName;

    private String variableTypeString;

    private Class<?> variableTypeClass;

    private Object variableValue;

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
}
