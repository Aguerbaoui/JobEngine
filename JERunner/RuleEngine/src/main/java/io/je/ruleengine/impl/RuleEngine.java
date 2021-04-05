package io.je.ruleengine.impl;

import io.je.ruleengine.models.Rule;
import io.je.utilities.beans.JEData;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.runtimeobject.JEObject;

import java.util.List;
import java.util.Set;

/*
 * This class handles all the rule engine operations.
 */
public class RuleEngine {
	private static ProjectContainerRepository projectManager = new ProjectContainerRepository();

	public RuleEngine() {

	}

	/*
	 * add a new rule to the rule engine
	 */
	public static void addRule(Rule rule) throws RuleAlreadyExistsException, RuleCompilationException,
			JEFileNotFoundException {

		String projectId = rule.getJobEngineProjectID();
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		project.addRule(rule);

	}

	public static void updateRule(Rule rule) throws RuleCompilationException, JEFileNotFoundException {


		String projectId = rule.getJobEngineProjectID();
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		project.updateRule(rule);

	}

	public static boolean fireRules(String projectId)
			throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {

		ProjectContainer project = projectManager.getProjectContainer(projectId);
		project.fireRules();
		return true;
	}

	public static boolean stopRuleExecution(String projectId) {

		ProjectContainer project = projectManager.getProjectContainer(projectId);
		return project.stopRuleExecution();
	}

	public static boolean fireRules(String projectId, List<Rule> rules, boolean removePreviouslyAddedRules) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void deleteProjectRules(String projectId) {
		//TODO confirm with manél
		projectManager.deleteProjectContainer(projectId);
	}


	public boolean addRules(List<Rule> rules) throws RuleAlreadyExistsException, RuleCompilationException,
			JEFileNotFoundException, RuleNotAddedException {

		// TODO: try/catch errors and return list of the rules that were not added
		for (Rule rule : rules) {
			addRule(rule);
		}
		return true;
	}

	public static void compileRule(Rule rule) throws RuleCompilationException, JEFileNotFoundException {


		String projectID = rule.getJobEngineProjectID();
		ProjectContainer project = projectManager.getProjectContainer(projectID);
		project.compileRule(rule);
		
	}


 /*   public static boolean addTopics(String projectId, Set<String> topics) throws ProjectNotFoundException {


        ProjectContainer project = projectManager.getProjectContainer(projectId);
        if (project == null) {
            throw new ProjectNotFoundException(Errors.projectNotFound);
        }
        while (topics.iterator().hasNext()) {
            String next = topics.iterator().next();
            project.addTopic(next);
        }

        return true;
    } */



	public boolean compileRules(List<Rule> rules) throws RuleCompilationException, JEFileNotFoundException {

		for (Rule rule : rules) {
			compileRule(rule);
		}
		return true;
	}

	public static void deleteRule(String projectId,String ruleId) throws DeleteRuleException {
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		project.deleteRule(ruleId);
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

	public boolean insertFact(JEObject fact) {

		ProjectContainer project = projectManager.getProjectContainer(fact.getJobEngineProjectID());
		project.insertFact(fact);
		return false;
	}

	public boolean retractFact(JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateFact(JEObject fact) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void buildProject(String projectId) throws RuleBuildFailedException {

		ProjectContainer project = projectManager.getProjectContainer(projectId);
		project.buildProject();

	}

	public static void assertFact(String projectId, JEObject fact) {
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		project.insertFact(fact);
		
	}

	public static Rule getRule(String projectId, String ruleId) {
		ProjectContainer project = projectManager.getProjectContainer(projectId);
		return project.getRule(ruleId);
	}



}
