package io.je.utilities.beans;

import io.je.utilities.models.EventType;
import io.je.utilities.runtimeobject.JEObject;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Document(collection="JEEvent")
public class JEEvent extends JEObject {

   

    private String name;

    private String triggeredById;

    private EventType type;

    private boolean isTriggered = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTriggeredById() {
        return triggeredById;
    }

    public void setTriggeredById(String triggeredById) {
        this.triggeredById = triggeredById;
    }

    public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public JEEvent() {}

    public JEEvent(String jobEngineElementID, String jobEngineProjectID, String name, EventType type) {
        super(jobEngineElementID, jobEngineProjectID);
        this.name = name;
        this.type = type;
    }

	public boolean isTriggered() {
		return isTriggered;
	}

	public void setTriggered(boolean isTriggered) {
		this.isTriggered = isTriggered;
	}
    
    
    
}
