package io.je.runtime.beans;

public class DMListener {
	
	String id;
	String projectId;
	String type; // rule/wf
	//boolean isActive = true;
	
	
	
	
	public DMListener(String id, String projectId, String type) {
		super();
		this.id = id;
		this.projectId = projectId;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/*public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	*/

}
