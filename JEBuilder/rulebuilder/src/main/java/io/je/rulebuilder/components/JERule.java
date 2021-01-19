package io.je.rulebuilder.components;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.runtimeobject.JEObject;

@Document(collection="JERule")
public abstract class JERule extends JEObject  {
	
	/*
	 * buildStatus
	 */
	boolean isBuilt = false;
	
	/*
	 * check if rule was added to JERunner or not
	 */
	boolean isAdded =  false;
	
	//public abstract String generateDRL(String buildPath);

	public boolean isBuilt() {
		return isBuilt;
	}

	
	
	public boolean isAdded() {
		return isAdded;
	}



	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}



	public void setBuilt(boolean isBuilt) {
		this.isBuilt = isBuilt;
	}

	public JERule(String jobEngineElementID, String jobEngineProjectID) {
		super(jobEngineElementID, jobEngineProjectID);
	}

	public JERule() {
		// TODO Auto-generated constructor stub
	}

	

}
