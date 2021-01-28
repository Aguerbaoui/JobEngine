package io.je.runtime.models;

public class EventModel {

    public String type;

    public String eventId;

    public EventModel(String type, String eventId) {
        super();
        this.type = type;
        this.eventId = eventId;
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
