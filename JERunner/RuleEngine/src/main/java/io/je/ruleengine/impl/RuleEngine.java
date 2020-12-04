package io.je.ruleengine.impl;



import java.util.List;
import io.je.ruleengine.interfaces.ProjectContainerRepositoryInterface;
import io.je.ruleengine.interfaces.RuleEngineInterface;
import io.je.ruleengine.models.Rule;
import io.je.utilities.exceptions.RuleCompilationException;
import io.je.utilities.exceptions.RuleEngineBuildFailedException;
import io.je.utilities.exceptions.RulesNotFiredException;
import io.je.utilities.runtimeobject.JEObject;


public class RuleEngine implements RuleEngineInterface{
	private static ProjectContainerRepositoryInterface projectManager = new ProjectContainerRepository();
	
	public RuleEngine()
	{
		 
	}

	public boolean addRule(Rule rule) {
		 
		String projectID = rule.getJobEngineProjectID();
		ProjectContainer project = projectManager.getProjectContainer(projectID);
		try {
			project.addRule(rule);
		} catch (RuleCompilationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return true;
		
	}

	public boolean addRules(List<Rule> rules) {
		 
		for(Rule rule: rules)
		{
			addRule(rule);
		}
		return true;
	}

	public boolean updateRule(Rule rule) {
		 
		String projectID = rule.getJobEngineProjectID();
		ProjectContainer project = projectManager.getProjectContainer(projectID);
		// project.updateRule(rule);
		return true;
	}

	public boolean compileRule(Rule rule) {
		 
		String projectID = rule.getJobEngineProjectID();
		ProjectContainer project = projectManager.getProjectContainer(projectID);
		//project.compileRule(rule);
		return true;
	}

	public boolean compileRules(List<Rule> rules) {
		 
		for(Rule rule: rules)
		{
			compileRule(rule);
		}
		return true;
	}

	public boolean deleteRule(String ruleID) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteRules(List<String> rulesIDs) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean disableRule(String ruleID) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean enableRule(String ruleID) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean fireRules(String projectId)  {
		 
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		try {
			project.fireRules();
		} catch (RulesNotFiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuleEngineBuildFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean stopRuleExecution(String projectId) {
		 
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		return project.stopRuleExecution();
	}



	@Override
	public boolean fireRules(String projectId, List<Rule> rules, boolean removePreviouslyAddedRules) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertFact( JEObject fact) {
		 
		ProjectContainer project = projectManager.getProjectContainer(fact.getJobEngineProjectID());
		project.insertFact(fact);
		return false;
	}

	@Override
	public boolean retractFact( JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateFact( JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}



}
