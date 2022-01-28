package io.je.project.beans.project.request;

import java.util.Map;

public class ProjectRequestObject {
	ProjectActionEnum type;
	String projectId;
	Map<String, String> dicOfValues;
	public ProjectRequestObject() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ProjectRequestObject(ProjectActionEnum type, String projectId, Map<String, String> dicOfValues) {
		super();
		this.type = type;
		this.projectId = projectId;
		this.dicOfValues = dicOfValues;
	}
	
	public ProjectActionEnum getType() {
		return type;
	}
	public void setType(ProjectActionEnum type) {
		this.type = type;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public Map<String, String> getDicOfValues() {
		return dicOfValues;
	}
	public void setDicOfValues(Map<String, String> dicOfValues) {
		this.dicOfValues = dicOfValues;
	}
	
	

}