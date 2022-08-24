package io.je.classbuilder.models;

public class ModelUpdate {

    DataModelAction action;
    ClassDefinition model;
    String workspaceName;

    public ModelUpdate() {

    }

    public ModelUpdate(DataModelAction action, ClassDefinition model) {
        super();
        this.action = action;
        this.model = model;
    }

    public DataModelAction getAction() {
        return action;
    }

    public void setAction(DataModelAction action) {
        this.action = action;
    }

    public ClassDefinition getModel() {
        return model;
    }

    public void setModel(ClassDefinition model) {
        this.model = model;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }


}
