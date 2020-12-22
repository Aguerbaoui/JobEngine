package io.je.rulebuilder.components;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.utilities.runtimeobject.JEObject;

import java.util.ArrayList;
import java.util.List;

/*
 * rule definition in job engine
 */
public class JERule extends JEObject {

	String projectId;
	String ruleId;
	String salience;
	boolean enabled;
	String dateEffective;
	String dateExpires;
	String duration;
	String timer;
	String calendar;
    Condition condition;

    List<Consequence> consequences;


    
    public JERule(String jobEngineElementID, String jobEngineProjectID) {
        super(jobEngineElementID, jobEngineProjectID);
        consequences = new ArrayList<>();
    }


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


	public Condition getCondition() {
		return condition;
	}


	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public void setCondition(PersistableBlock rootBlock) {
		this.condition = new Condition(rootBlock);
	}
	



	public List<Consequence> getConsequences() {
		return consequences;
	}


	public void setConsequences(List<Consequence> consequences) {
		this.consequences = consequences;
	}
}