package io.je.utilities.models;

public class EventModel {

    public String type;

    public String eventId;

    public String name;

    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    private String reference;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventModel(String id, String projectId, String type, String reference, String name) {
        super();
        this.type = type;
        this.reference = reference;
        this.name = name;
        this.eventId = id;
        this.projectId = projectId;
    }

    public EventModel() {
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
