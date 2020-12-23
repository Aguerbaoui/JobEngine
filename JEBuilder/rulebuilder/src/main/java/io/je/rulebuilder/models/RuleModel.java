package io.je.rulebuilder.models;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.config.AttributesMapping;


/*
 * Rule Model Class
 */
public class RuleModel {
	
    @JsonProperty(AttributesMapping.PROJECTID)
	String projectId;
	
    @JsonProperty(AttributesMapping.RULEID)
    String ruleId;
	
    @JsonProperty(AttributesMapping.SALIENCE)
    String salience;

    @JsonProperty(AttributesMapping.ENABLED)
    boolean enabled;
	
    @JsonProperty(AttributesMapping.DATEEFFECTIVE)
    String dateEffective;
	
    @JsonProperty(AttributesMapping.DATEEXPIRES)
    String dateExpires;
	
    @JsonProperty(AttributesMapping.TIMER)
    String timer;
	

    
    
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
	public String getSalience() {
		return salience;
	}
	public void setSalience(String salience) {
		this.salience = salience;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getDateEffective() {
		return dateEffective;
	}
	public void setDateEffective(String dateEffective) {
		this.dateEffective = dateEffective;
	}
	public String getDateExpires() {
		return dateExpires;
	}
	public void setDateExpires(String dateExpires) {
		this.dateExpires = dateExpires;
	}

	public String getTimer() {
		return timer;
	}
	public void setTimer(String timer) {
		this.timer = timer;
	}


	
	

}
