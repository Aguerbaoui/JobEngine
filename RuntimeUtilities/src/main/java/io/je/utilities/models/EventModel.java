package io.je.utilities.models;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.utilities.beans.JEEvent;
import io.je.utilities.config.Utility;
import io.je.utilities.mapping.EventModelMapping;
import io.je.utilities.time.JEDate;

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
    boolean triggered;
      
    @JsonProperty(EventModelMapping.TIMOUTVALUE)
    private int timeout ;
    
    @JsonProperty(EventModelMapping.TIMEOUTUNIT)
    private String timeoutUnit;

    private String createdBy;
   
    private String modifiedBy;

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

  /*  public EventModel(String id, String projectId, EventType type, String name) {
        super();
        this.name = name;
        this.eventId = id;
        this.projectId = projectId;
    }
*/
    public EventModel() {
        super();
    }

	public EventModel(JEEvent event) {
		this.name = event.getName();
        this.eventId = event.getJobEngineElementID();
        this.projectId = event.getJobEngineProjectID();
        this.description = event.getDescription();
        this.createdAt = JEDate.formatDateToSIOTHFormat(event.getJeObjectCreationDate());
		this.lastModifiedAt = JEDate.formatDateToSIOTHFormat(event.getJeObjectLastUpdate());
        this.triggered = event.isTriggered();
        this.timeout = event.getTimeoutValue();
        this.timeoutUnit = event.getTimeoutUnit();
		this.createdBy = event.getJeObjectCreatedBy();
		this.modifiedBy = event.getJeObjectModifiedBy();
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

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(String lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public boolean getTriggered() {
		return triggered;
	}

	public void setTriggered(boolean triggered) {
		this.triggered = triggered;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getTimeoutUnit() {
		return timeoutUnit;
	}

	public void setTimeoutUnit(String timeoutUnit) {
		this.timeoutUnit = timeoutUnit;
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
    
    
    
}
