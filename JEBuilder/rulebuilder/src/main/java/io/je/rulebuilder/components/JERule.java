package io.je.rulebuilder.components;

import java.util.ArrayList;
import java.util.List;

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
	
	List<String> topics = new ArrayList<>();
	
	String description ;
	
	String ruleFrontConfig;
	
	public JERule(String jobEngineElementID, String jobEngineProjectID, String ruleName) {
		super(jobEngineElementID, jobEngineProjectID);
		this.ruleName = ruleName;
	}

	public JERule() {
	}


	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addTopic(String topic) {
		topics.add(topic);
	}

	public List<String> getTopics() {
		return topics;
	}



	
	

}
