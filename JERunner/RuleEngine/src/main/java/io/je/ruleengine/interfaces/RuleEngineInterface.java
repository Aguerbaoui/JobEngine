package io.je.ruleengine.interfaces;

import java.util.List;

import io.je.utilities.runtimeobject.JERuntimeObject;
import io.je.ruleengine.models.Rule;

public interface RuleEngineInterface {
	
	// rule management
	public boolean addRule(Rule rule);
	public boolean addRules(List<Rule> rules);	
	public boolean updateRule(Rule rule);	
	public boolean compileRule(Rule rule);
	public boolean compileRules(List<Rule> rules);
	public boolean deleteRule(String ruleID);
	public boolean deleteRules(List<String> rulesIDs);
	public boolean disableRule(String ruleID);
	public boolean enableRule(String ruleID);
	
	
	//firing rules 
	public boolean fireRules(String projectId);
	public boolean fireRules(String projectId,List<Rule> rules, boolean removePreviouslyAddedRules); 
	public boolean stopRuleExecution(String projectId);


	//FactManagement
	public boolean insertFact(JERuntimeObject fact);
	public boolean retractFact(JERuntimeObject fact);
	public boolean updateFact(JERuntimeObject fact);

	
	
	

}
