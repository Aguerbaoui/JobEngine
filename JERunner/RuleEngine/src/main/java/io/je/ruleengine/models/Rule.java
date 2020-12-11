package io.je.ruleengine.models;

import io.je.utilities.runtimeobject.JEObject;

/*
 *Rule Definition 
 */
public class Rule extends JEObject {
	
	

	public Rule(String jobEngineElementID, String jobEngineProjectID) {
		super(jobEngineElementID, jobEngineProjectID);
	}

	//Rule Name
	String name;

	//Rule Type ( drl, csv ...)
	Type resourceType;
	
	//Rule file path 
	String path;
	
	//Rule file content 
	String content;
	
	//Rule status
	Status status;
	
	


	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public Type getResourceType() {
		return resourceType;
	}




	public void setResourceType(Type resourceType) {
		this.resourceType = resourceType;
	}




	public String getPath() {
		return path;
	}




	public void setPath(String path) {
		this.path = path;
	}




	public String getContent() {
		return content;
	}




	public void setContent(String content) {
		this.content = content;
	}




	public Status getStatus() {
		return status;
	}




	public void setStatus(Status status) {
		this.status = status;
	}




	@Override
	public String toString() {
		return name + " " + path;

	}

}

enum Type {
	DRL, DTABLE, DSL, DRT,
}

enum Status {
	ENABLED, DISABLED,
}