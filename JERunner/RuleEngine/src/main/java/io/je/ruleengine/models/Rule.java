package io.je.ruleengine.models;

import io.je.ruleengine.enumerations.RuleFormat;
import io.je.utilities.runtimeobject.JEObject;

/*
 *Rule Definition 
 */
public class Rule extends JEObject {
	
	



	public Rule(String jobEngineElementID, String jobEngineProjectID, String name, RuleFormat resourceType,
			String path) {
		super(jobEngineElementID, jobEngineProjectID);
		this.name = name;
		this.resourceType = resourceType;
		this.path = path;
	}



	//Rule Name
	String name;

	//Rule Type ( drl, csv ...)
	RuleFormat resourceType;
	
	//Rule file path 
	String path;
	
	//Rule file content 
	String content;
	


	public String getName() {
		return name;
	}




	public void setName(String name) {
		this.name = name;
	}




	public RuleFormat getResourceType() {
		return resourceType;
	}




	public void setResourceType(RuleFormat resourceType) {
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





	@Override
	public String toString() {
		return name + " " + path;

	}

}

