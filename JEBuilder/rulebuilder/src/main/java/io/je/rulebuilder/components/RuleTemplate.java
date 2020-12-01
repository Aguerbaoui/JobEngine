package io.je.rulebuilder.components;

public class RuleTemplate {
	String ruleName;
	String duration;
	String salience;
	String condition;
	String consequence;
	
	
	
	public RuleTemplate(String ruleName, String duration, String salience, String condition, String consequence) {
		super();
		this.ruleName = ruleName;
		this.duration = duration;
		this.salience = salience;
		this.condition = condition;
		this.consequence = consequence;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getSalience() {
		return salience;
	}
	public void setSalience(String salience) {
		this.salience = salience;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getConsequence() {
		return consequence;
	}
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	
	
	
}
