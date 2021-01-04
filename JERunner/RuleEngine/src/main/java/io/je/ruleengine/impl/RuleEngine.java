package io.je.ruleengine.impl;


import io.je.ruleengine.models.Rule;
import io.je.utilities.exceptions.*;
import io.je.utilities.logger.JELogConstants;
import io.je.utilities.logger.JELogger;
import io.je.utilities.runtimeobject.JEObject;

import java.io.FileNotFoundException;
import java.util.List;

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
    public static boolean addRule(Rule rule) throws RuleAlreadyExistsException, RuleCompilationException, FileNotFoundException {

        String projectId;
        try {
            projectId = rule.getJobEngineProjectID();
        } catch (Exception e) {
            JELogger.error(RuleEngine.class, JELogConstants.idNotFound);
            return false;
        }
        if (projectId != null && !projectId.isEmpty()) {
            ProjectContainer project = projectManager.getProjectContainer(projectId);
            project.addRule(rule);
        } else {
            JELogger.error(RuleEngine.class, JELogConstants.idNotFound);
            return false;
        }
        return true;

    }

    public static boolean updateRule(Rule rule) throws RuleCompilationException, FileNotFoundException {

        String projectId;
        try {
            projectId = rule.getJobEngineProjectID();
        } catch (Exception e) {
            JELogger.error(RuleEngine.class, JELogConstants.idNotFound);
            return false;
        }
        if (projectId != null && !projectId.isEmpty()) {
            ProjectContainer project = projectManager.getProjectContainer(projectId);
            project.updateRule(rule);
        } else {
            JELogger.error(RuleEngine.class, JELogConstants.idNotFound);
            return false;
        }
        return true;
    }

    public static boolean fireRules(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {

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

    public boolean addRules(List<Rule> rules) throws RuleAlreadyExistsException, RuleCompilationException, FileNotFoundException {

        for (Rule rule : rules) {
            addRule(rule);
        }
        return true;
    }

    public boolean compileRule(Rule rule) throws RuleCompilationException, FileNotFoundException {

        String projectID = rule.getJobEngineProjectID();
        ProjectContainer project = projectManager.getProjectContainer(projectID);
        project.compileRule(rule);
        return true;
    }

    public boolean compileRules(List<Rule> rules) throws RuleCompilationException, FileNotFoundException {

        for (Rule rule : rules) {
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


}
