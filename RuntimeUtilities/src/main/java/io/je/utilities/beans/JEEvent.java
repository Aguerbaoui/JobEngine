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

    private String description;

    private boolean isTriggered = false;
    
    private int activeTriggersCount=0;
    
    private int timeout ;
    
    private String timeoutUnit;

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


	public void trigger()
	{
		isTriggered=true;
		activeTriggersCount++;
	}

	
	public void untrigger()
	{
		activeTriggersCount--;
		if(activeTriggersCount==0)
		{
			isTriggered=false;

		}
	}
	
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
