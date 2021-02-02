package io.je.rulebuilder.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.config.AttributesMapping;


/*
 * Rule Model Class
 */
public class RuleModel {
	

	
    @JsonProperty(AttributesMapping.RULEID)
    String ruleId;
    
    @JsonProperty(AttributesMapping.RULENAME)
    String ruleName;

    @JsonProperty(AttributesMapping.DESC)
    String description;
	
    
    @JsonProperty(AttributesMapping.SALIENCE)
    String salience;

    @JsonProperty(AttributesMapping.ENABLED)
    String enabled;
	
    @JsonProperty(AttributesMapping.DATEEFFECTIVE)
    String dateEffective;
	
    @JsonProperty(AttributesMapping.DATEEXPIRES)
    String dateExpires;
	
    @JsonProperty(AttributesMapping.TIMER)
    String timer;
	

    
    

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
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
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
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	
	

}
