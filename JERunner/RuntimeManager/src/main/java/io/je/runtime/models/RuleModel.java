package io.je.runtime.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.ruleengine.enumerations.RuleFormat;
import io.je.runtime.config.RuleModelMapping;


/*
 * Rule Model
 */
public class RuleModel {
	
	//The project this rule belongs to 
	@JsonProperty(RuleModelMapping.PROJECTID)
	String projectId;
	
	//rule identifier
	@JsonProperty(RuleModelMapping.RULEID)
	String ruleId;
	
	//path where the rule file was created
	@JsonProperty(RuleModelMapping.PATH)
	String rulePath;
	
	//Rule format
	@JsonProperty(RuleModelMapping.FORMAT)
	RuleFormat format;
	
	
	/*
	 * getters and setters
	 */

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRulePath() {
		return rulePath;
	}

	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}

	public RuleFormat getFormat() {
		return format;
	}

	public void setFormat(RuleFormat format) {
		this.format = format;
	}

	

	
	
	
	

}
