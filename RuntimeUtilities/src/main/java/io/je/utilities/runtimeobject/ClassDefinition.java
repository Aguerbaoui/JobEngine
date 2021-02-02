package io.je.utilities.runtimeobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.utilities.mapping.ClassDefinitionMapping;

public class ClassDefinition {
	
	
    @JsonProperty(ClassDefinitionMapping.WORKSPACEID)
	String workspaceId;	
    
    @JsonProperty(ClassDefinitionMapping.CLASSID)
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
	private ClassDefinition() {
	}
	
	
	
	

}
