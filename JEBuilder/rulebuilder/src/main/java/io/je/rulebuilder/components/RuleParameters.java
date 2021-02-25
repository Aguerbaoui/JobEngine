package io.je.rulebuilder.components;

public class RuleParameters {

	/*
	 * rule priority
	 */
	private String salience;

	/*
	 * rule parameter that indicates whether a rule is enabled or disabled
	 */
	private String enabled;

	/*
	 * rule parameter that indicates when the rule should be activated
	 */
	private String dateEffective;

	/*
	 * rule parameter that indicates when the rule expires
	 */
	private String dateExpires;

	/*
	 * cron expression that defines the rule's firing schedule
	 */
	private String timer;

	
	
	
	//getter and setters
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
	
	
}
