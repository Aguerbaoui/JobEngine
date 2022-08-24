package io.je.utilities.beans;

public class InformModel {
    public String message;

    public String projectName;

    public String workflowName;

    public InformModel() {
    }

    public InformModel(String message, String projectName, String workflowName) {
        this.message = message;
        this.projectName = projectName;
        this.workflowName = workflowName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    @Override
    public String toString() {
        return "InformModel{" +
                "message='" + message + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}
