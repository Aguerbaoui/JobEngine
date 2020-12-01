package io.je.utilities.runtimeobject;

import java.time.LocalDateTime;

public abstract class JEObject {

	protected String jobEngineElementID;
	protected String jobEngineProjectID;
	protected LocalDateTime jeObjectLastUpdate;
	

	
	public String getId() {
		return jobEngineElementID;
	}
	public void setId(String jobEngineElementID) {
		this.jobEngineElementID = jobEngineElementID;
	}
	public String getProjectId() {
		return jobEngineProjectID;
	}
	public void setProjectId(String jobEngineProjectID) {
		this.jobEngineProjectID = jobEngineProjectID;
	}
	public LocalDateTime getLastUpdate() {
		return jeObjectLastUpdate;
	}
	public void setLastUpdate(LocalDateTime jeObjectLastUpdate) {
		this.jeObjectLastUpdate = jeObjectLastUpdate;
	}
	
	
}
