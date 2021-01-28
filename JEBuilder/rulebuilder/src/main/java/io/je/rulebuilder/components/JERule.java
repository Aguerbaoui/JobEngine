package io.je.rulebuilder.components;

import java.util.List;

import org.json.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;

import io.je.utilities.runtimeobject.ClassDefinition;
import io.je.utilities.runtimeobject.JEObject;

@Document(collection="JERule")
public abstract class JERule extends JEObject  {
	
	
	/*
	 * rule name
	 */
	String ruleName;
	
	/*
	 * buildStatus
	 */
	boolean isBuilt = false;
	
	/*
	 * check if rule was added to JERunner or not
	 */
	boolean isAdded =  false;
	
	
	String ruleFrontConfig;
	
	public JERule(String jobEngineElementID, String jobEngineProjectID, String ruleName) {
		super(jobEngineElementID, jobEngineProjectID);
		this.ruleName = ruleName;
	}

	public JERule() {
	}


	public abstract List<ClassDefinition> getTopics();
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

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

	public String getRuleFrontConfig() {
		return ruleFrontConfig;
	}

	public void setRuleFrontConfig(String ruleFrontConfig) {
		this.ruleFrontConfig = ruleFrontConfig;
	}



	

}
