package io.je.utilities.runtimeobject;

import java.time.LocalDateTime;

public abstract class JEObject {

	protected String id;
	protected String projectId;
	protected LocalDateTime lastUpdate;
	

	
	public String getId() {
		return id;
	}
	public void setId(String jobEngineElementID) {
		this.id = jobEngineElementID;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String jobEngineProjectID) {
		this.projectId = jobEngineProjectID;
	}
	public LocalDateTime getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(LocalDateTime jeObjectLastUpdate) {
		this.lastUpdate = jeObjectLastUpdate;
	}
	
	
}
