package io.je.utilities.beans;

import io.je.utilities.models.EventType;
import io.je.utilities.runtimeobject.JEObject;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "JEEvent")
public class JEEvent extends JEObject {

	private String name;

	private String triggeredById;

	private EventType type;

	private String description;

	private boolean isTriggered = false;

	private long lastTriggerTime;


	private int timeoutValue;

	private String timeoutUnit;

	private int timeout; // milliseconds

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

	@SuppressWarnings("unused")
	private JEEvent() {

	}


	public long getLastTriggerTime() {
		return lastTriggerTime;
	}

	public void setLastTriggerTime(long lastTriggerTime) {
		this.lastTriggerTime = lastTriggerTime;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public JEEvent(String jobEngineElementID, String jobEngineProjectID, String name, EventType type,
			String description, int timeoutValue, String timeoutUnit) {
		super(jobEngineElementID, jobEngineProjectID);
		this.name = name;
		this.type = type;
		this.triggeredById = jobEngineElementID;
		this.description = description;
		this.timeoutValue = timeoutValue;
		this.timeoutUnit = timeoutUnit;
		setTimeout();
	}

	private void setTimeout() {
		if (timeoutValue != 0) {
			switch (timeoutUnit) {
				case "s": 
					timeout = timeoutValue * 1000;
					break;
				case "m":
					timeout = timeoutValue * 60 *1000;
					break;
				case "h":
					timeout = timeoutValue * 3600 * 1000;
					break;
				default :
					timeout = timeoutValue * 1000;
					break;
			}
		}

	}

	public boolean isTriggered() {
		return isTriggered;
	}

	public void trigger() {
		isTriggered = true;
		lastTriggerTime = System.nanoTime();
		

	}

	public void setTriggered(boolean isTriggered) {
		this.isTriggered = isTriggered;
	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTimeoutValue() {
		return timeoutValue;
	}

	public void setTimeoutValue(int timeoutValue) {
		this.timeoutValue = timeoutValue;
		setTimeout();

	}

	public String getTimeoutUnit() {
		return timeoutUnit;
	}

	public void setTimeoutUnit(String timeoutUnit) {
		this.timeoutUnit = timeoutUnit;
	}

}
