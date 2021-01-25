package io.je.utilities.runtimeobject;


public class ClassDefinition {
	
	String workspaceId;
	String classId;
	public ClassDefinition(String workspaceId, String classId) {
		super();
		this.workspaceId = workspaceId;
		this.classId = classId;
	}
	public String getWorkspaceId() {
		return workspaceId;
	}
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	
	
	
	

}
