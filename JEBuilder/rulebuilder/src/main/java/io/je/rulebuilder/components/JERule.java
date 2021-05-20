package io.je.rulebuilder.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

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
	

	private Map<String,Integer> topics = new HashMap<>();
	
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
		if(!topics.containsKey(topic))
		{
			topics.put(topic,1);
		}
		else {
			topics.put(topic,topics.get(topic)+1);
		}

	}
	
	
	public void updateTopic(String oldTopic, String newTopic) {
			removeTopic(oldTopic);
			addTopic(newTopic);

		
	}
	
	public void removeTopic(String topic)
	{
		if(topics.containsKey(topic))
		{
			topics.put(topic,topics.get(topic)-1);
			if(topics.get(topic)==0)
			{
				topics.remove(topic);
			}
		}
	}

	public Map<String, Integer> getTopics() {
		return topics;
	}

	public void setTopics(Map<String, Integer> topics) {
		this.topics = topics;
	}


	
		




	
	

}
