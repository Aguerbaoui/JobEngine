package io.je.ruleengine.models;

import io.je.utilities.runtimeobject.JEObject;

public class Rule extends JEObject{
	
	String name;
	Type resourceType;
	String path; 
	Status state; 
	String content;
	
	
	public Type getResourceType() {
		return resourceType;
	}
	public void setResourceType(Type resourceType) {
		this.resourceType = resourceType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Status getState() {
		return state;
	}
	public void setState(Status state) {
		this.state = state;
	}
	
	@Override
	public String toString()
	{
		return name+" "+path;
		
	}
	

}

enum Type{
	DRL,
	DTABLE,
	DSL,
	DRT,
	}

enum Status{
	ENABLED,
	DISABLED,
}