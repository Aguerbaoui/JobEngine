package io.je.utilities.beans;

public class InformModel {
    public String message;

    public String projectName;

    public InformModel() {}
    public InformModel(String message, String projectName) {
        this.message = message;
        this.projectName = projectName;
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

    @Override
    public String toString() {
        return "InformModel{" +
                "message='" + message + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}
