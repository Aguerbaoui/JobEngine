package io.je.utilities.beans;

public class JEVariableMessage {

    String variableName;
    String variableValue;


    private JEVariableMessage() {
        // TODO Auto-generated constructor stub
    }


    public JEVariableMessage(String variableName, String variableValue) {
        super();
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }


}
