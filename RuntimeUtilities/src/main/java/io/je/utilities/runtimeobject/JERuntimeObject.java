package io.je.utilities.runtimeobject;

import java.time.LocalDateTime;

public abstract class JERuntimeObject {

	protected String jobEngineElementID;
	protected String jobEngineProjectID;
	protected LocalDateTime jeObjectLastUpdate;
	
	public JERuntimeObject()
	{
		//TODO: add time config (format, timezone, etc ..)
		//set update time
	}
	
	public String getJobEngineElementID() {
		return jobEngineElementID;
	}
	public void setJobEngineElementID(String jobEngineElementID) {
		this.jobEngineElementID = jobEngineElementID;
	}
	public String getJobEngineProjectID() {
		return jobEngineProjectID;
	}
	public void setJobEngineProjectID(String jobEngineProjectID) {
		this.jobEngineProjectID = jobEngineProjectID;
	}
	public LocalDateTime getJeObjectLastUpdate() {
		return jeObjectLastUpdate;
	}
	public void setJeObjectLastUpdate(LocalDateTime jeObjectLastUpdate) {
		this.jeObjectLastUpdate = jeObjectLastUpdate;
	}
	
	
}
