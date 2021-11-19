package io.je.utilities.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import io.siothconfig.SIOTHConfigUtility;

public class WorkflowModel {

    private String projectId;

    private String id;

    private String path;

    private String name;

    private String description;

    private boolean triggeredByEvent;

    private boolean onProjectBoot;

    private String endBlockEventId;

    private String triggerMessage;

    private ArrayList<EventModel> events;

    private ArrayList<TaskModel> tasks;
    
    private   String createdBy;
    
    private String modifiedBy;

    private String status;

    private String modifiedAt;

    private String createdAt;

    private String frontConfig;

    private boolean enabled;

    public WorkflowModel(String projectId, String id, String path, String name, ArrayList<EventModel> events,
                         ArrayList<TaskModel> tasks) {
        super();
        this.projectId = projectId;
        this.id = id;
        this.path = path;
        this.events = events;
        this.tasks = tasks;
        this.name = name;
    }

    public String getTriggerMessage() {
        return triggerMessage;
    }

    public void setTriggerMessage(String triggerMessage) {
        this.triggerMessage = triggerMessage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTriggeredByEvent() {
        return triggeredByEvent;
    }

    public void setTriggeredByEvent(boolean triggeredByEvent) {
        this.triggeredByEvent = triggeredByEvent;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isOnProjectBoot() {
        return onProjectBoot;
    }

    public void setOnProjectBoot(boolean onProjectBoot) {
        this.onProjectBoot = onProjectBoot;
    }

    
    
    public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFrontConfig() {
        return frontConfig;
    }

    public void setFrontConfig(String frontConfig) {
        this.frontConfig = frontConfig;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndBlockEventId() {
        return endBlockEventId;
    }

    public void setEndBlockEventId(String endBlockEventId) {
        this.endBlockEventId = endBlockEventId;
    }

    @Override
    public String toString() {
        return "WorkflowModel{" +
                "projectId='" + projectId + '\'' +
                ", id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", triggeredByEvent=" + triggeredByEvent +
                ", onProjectBoot=" + onProjectBoot +
                ", endBlockEventId='" + endBlockEventId + '\'' +
                ", triggerMessage='" + triggerMessage + '\'' +
                ", events=" + events +
                ", tasks=" + tasks +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", status='" + status + '\'' +
                ", modifiedAt='" + modifiedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", frontConfig='" + frontConfig + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
