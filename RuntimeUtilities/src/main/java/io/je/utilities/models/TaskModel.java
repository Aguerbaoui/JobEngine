package io.je.utilities.models;

public class TaskModel {

    public String taskId;

    public String type;

    public TaskModel(String taskId, String type) {
        super();
        this.taskId = taskId;
        this.type = type;
    }

    public TaskModel() {
        super();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
