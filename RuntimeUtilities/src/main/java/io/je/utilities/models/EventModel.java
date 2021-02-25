package io.je.utilities.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.utilities.beans.JEEvent;
import io.je.utilities.mapping.EventModelMapping;

@JsonInclude(Include.NON_NULL)
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

    @JsonProperty(EventModelMapping.CREATEDAT)
    String createdAt;
    
    @JsonProperty(EventModelMapping.LASTUPDATE)
    String lastModifiedAt;
    
    @JsonProperty(EventModelMapping.TRIGGERED)
    String triggered;
    
    
    
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

	public EventModel(JEEvent event) {
		this.name = event.getName();
        this.eventId = event.getJobEngineElementID();
        this.projectId = event.getJobEngineProjectID();
        this.description = event.getDescription();
        this.createdAt = event.getJeObjectCreationDate().toString();
        this.lastModifiedAt = event.getJeObjectLastUpdate().toString();
        this.triggered = String.valueOf(event.isTriggered());
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
