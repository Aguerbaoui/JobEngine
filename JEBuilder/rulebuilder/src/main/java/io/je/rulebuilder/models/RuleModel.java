package io.je.rulebuilder.models;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.config.AttributesMapping;
import io.je.utilities.config.Utility;


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
	
    @JsonProperty(AttributesMapping.CREATEDAT)
    String createdAt;
    
    @JsonProperty(AttributesMapping.LASTUPDATE)
    String lastModifiedAt;
    
    @JsonProperty(AttributesMapping.BUILDSTATUS)
    String isBuilt;
    
    
    //temporary 
    @JsonProperty(AttributesMapping.FRONTCONFIG)
    String ruleFrontConfig;
    
    
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.getSiothConfig().getDateFormat());


    
    public RuleModel(JERule rule) {
		super();
		this.ruleId = rule.getJobEngineElementID();
		this.ruleName = rule.getRuleName();
		this.description = rule.getDescription();
		this.isBuilt = String.valueOf(rule.isBuilt());
		this.createdAt = rule.getJeObjectCreationDate().format(formatter);
		this.lastModifiedAt = rule.getJeObjectLastUpdate().format(formatter);
		if(rule instanceof UserDefinedRule) {
			this.salience = ((UserDefinedRule)rule).getRuleParameters().getSalience();
			this.enabled = ((UserDefinedRule)rule).getRuleParameters().getEnabled();
			this.dateEffective = ((UserDefinedRule)rule).getRuleParameters().getDateEffective();
			this.dateExpires = ((UserDefinedRule)rule).getRuleParameters().getDateExpires();
			this.timer = ((UserDefinedRule)rule).getRuleParameters().getTimer();
			this.ruleFrontConfig = ((UserDefinedRule)rule).getRuleFrontConfig();

		}
		
	}



	public RuleModel() {
		super();
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



	public String getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}



	public String getLastModifiedAt() {
		return lastModifiedAt;
	}



	public void setLastModifiedAt(String lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}



	public String getIsBuilt() {
		return isBuilt;
	}



	public void setIsBuilt(String isBuilt) {
		this.isBuilt = isBuilt;
	}



	public String getRuleFrontConfig() {
		return ruleFrontConfig;
	}



	public void setRuleFrontConfig(String ruleFrontConfig) {
		this.ruleFrontConfig = ruleFrontConfig;
	}


	
	

}
