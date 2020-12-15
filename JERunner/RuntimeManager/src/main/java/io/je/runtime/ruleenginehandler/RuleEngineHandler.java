package io.je.runtime.ruleenginehandler;


import java.io.FileNotFoundException;

import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;
import io.je.runtime.models.RuleModel;
import io.je.utilities.exceptions.ProjectAlreadyRunningException;
import io.je.utilities.exceptions.RuleAlreadyExistsException;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleEngineBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;
import io.je.utilities.exceptions.RulesNotFiredException;

/*
 * class responsible for Rule Engine calls
 */
public class RuleEngineHandler {

	

	public RuleEngineHandler() {

	}
	
	
	/*
	 * add rule to rule engine
	 */
	public void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException, RuleNotAddedException, FileNotFoundException
	{
		
		//TODO: add test to check that models params are not null
		String ruleId = ruleModel.getProjectId()+"_"+ruleModel.getRuleId();
		Rule rule = new Rule(ruleId,ruleModel.getProjectId(),ruleModel.getRuleId(),ruleModel.getFormat(),ruleModel.getRulePath());
		if(!RuleEngine.addRule(rule))
		{
			throw new RuleNotAddedException("","");
		}
		
	}
	
	/*
	 * update rule in rule engine
	 */
	public void updateRule(RuleModel ruleModel) throws RuleCompilationException, FileNotFoundException 
	{
		
		//TODO: add test to check that models params are not null
		String ruleId = ruleModel.getProjectId()+"_"+ruleModel.getRuleId();
		Rule rule = new Rule(ruleId,ruleModel.getProjectId(),ruleModel.getRuleId(),ruleModel.getFormat(),ruleModel.getRulePath());
		RuleEngine.updateRule(rule);
		
	}
	
	
	/*
	 * start running a project given a project id 
	 */
	public void runRuleEngineProject(String projectId) throws RulesNotFiredException, RuleEngineBuildFailedException, ProjectAlreadyRunningException
	{
		RuleEngine.fireRules(projectId);
	}
	
	
	/*
	 * stop running a project given a project id 
	 */
	public void stopRuleEngineProjectExecution(String projectId) throws RulesNotFiredException, RuleEngineBuildFailedException, ProjectAlreadyRunningException
	{
		RuleEngine.stopRuleExecution(projectId);
	}
	


}
