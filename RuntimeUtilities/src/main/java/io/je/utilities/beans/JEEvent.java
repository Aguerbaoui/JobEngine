package io.je.utilities.beans;

import io.je.utilities.models.EventType;
import io.je.utilities.runtimeobject.JEObject;

import java.sql.Timestamp;

public class JEEvent extends JEObject {

   

    private String name;

    private String triggeredById;

    private EventType type;

    private boolean isTriggered = false;

    /*
    * The reference in case of a rule should be the rule id, in case of a workflow its the message or signal reference
    * */
    private String reference;

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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

	public boolean isTriggered() {
		return isTriggered;
	}

	public void setTriggered(boolean isTriggered) {
		this.isTriggered = isTriggered;
	}
    
    
    
}
