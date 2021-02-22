package io.je.utilities.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.je.utilities.mapping.EventModelMapping;


public class EventModel {

    @JsonProperty(EventModelMapping.EVENTID)
    private String eventId;

    @JsonProperty(EventModelMapping.EVENTNAME)
    private String name;

    @JsonProperty(EventModelMapping.PROJECTID)
    private String projectId;

    @JsonProperty(EventModelMapping.EVENTTYPE)
    private String eventType;

    @JsonProperty(EventModelMapping.DESCRIPTION)
    private String description;

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

    public EventModel(String id, String projectId, EventType type, String name) {
        super();
        this.name = name;
        this.eventId = id;
        this.projectId = projectId;
    }

    public EventModel() {
        super();
    }

	public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
