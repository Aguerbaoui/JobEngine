package io.je.utilities.models;

import java.util.HashMap;

public class TaskModel {

    public String taskId;

    public String type;

    public String taskName;

    public String taskDescription;

    public HashMap<String, Object> attributes;


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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

}
