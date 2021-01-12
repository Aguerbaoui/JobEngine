package io.je.runtime.models;

import java.util.ArrayList;

public class WorkflowModel {

    private String projectId;

    private String key;

    private String path;



    private String name;

    private ArrayList<EventModel> events;

    private ArrayList<TaskModel> tasks;

    public WorkflowModel(String projectId, String key, String path, String name, ArrayList<EventModel> events,
                         ArrayList<TaskModel> tasks) {
        super();
        this.projectId = projectId;
        this.key = key;
        this.path = path;
        this.events = events;
        this.tasks = tasks;
        this.name = name;
    }

    public WorkflowModel() {
        super();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<EventModel> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<EventModel> events) {
        this.events = events;
    }

    public ArrayList<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskModel> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "WorkflowModel [projectId=" + projectId + ", key=" + key + ", path=" + path + ", events=" + events
                + ", tasks=" + tasks + "]";
    }

}
