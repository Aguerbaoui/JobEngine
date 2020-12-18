package io.je.rulebuilder.models;

import java.util.ArrayList;


/*
 * Rule Model Class
 */
public class RuleModel {
	
	String projectId;
	String ruleId;
	String salience;
	boolean enabled;
	String dateEffective;
	String dateExpires;
	String duration;
	String timer;
	String calendar;
	ArrayList<String> conditionBlockRootIds;
	ArrayList<String> consequenceBlocks;
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getTimer() {
		return timer;
	}
	public void setTimer(String timer) {
		this.timer = timer;
	}
	public String getCalendar() {
		return calendar;
	}
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
	public ArrayList<String> getConditionBlockRootIds() {
		return conditionBlockRootIds;
	}
	public void setConditionBlockRootIds(ArrayList<String> conditionBlockRootIds) {
		this.conditionBlockRootIds = conditionBlockRootIds;
	}
	public ArrayList<String> getConsequenceBlocks() {
		return consequenceBlocks;
	}
	public void setConsequenceBlocks(ArrayList<String> consequenceBlocks) {
		this.consequenceBlocks = consequenceBlocks;
	}
	
	
	

}
