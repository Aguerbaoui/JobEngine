package io.je.runtime.ruleenginehandler;


import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;
import io.je.runtime.models.RuleModel;
import io.je.utilities.beans.JEData;
import io.je.utilities.exceptions.*;

import java.io.FileNotFoundException;

/*
 * class responsible for Rule Engine calls
 */
public class RuleEngineHandler {


    public RuleEngineHandler() {

    }


    /*
     * add rule to rule engine
     */
    public void addRule(RuleModel ruleModel) throws RuleAlreadyExistsException, RuleCompilationException, RuleNotAddedException, FileNotFoundException, ProjectNotFoundException {

        //TODO: add test to check that models params are not null
        String ruleId = ruleModel.getProjectId() + "_" + ruleModel.getRuleId();
        Rule rule = new Rule(ruleId, ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        if (!RuleEngine.addRule(rule) || !RuleEngine.addTopics(ruleModel.getProjectId(), ruleModel.getTopics())) {
            throw new RuleNotAddedException( "");
        }

    }

    /*
     * update rule in rule engine
     */
    public void updateRule(RuleModel ruleModel) throws RuleCompilationException, FileNotFoundException {

        //TODO: add test to check that models params are not null
        String ruleId = ruleModel.getProjectId() + "_" + ruleModel.getRuleId();
        Rule rule = new Rule(ruleId, ruleModel.getProjectId(), ruleModel.getRuleId(), ruleModel.getFormat(), ruleModel.getRulePath());
        RuleEngine.updateRule(rule);

    }


    /*
     * start running a project given a project id
     */
    public void runRuleEngineProject(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
        RuleEngine.fireRules(projectId);
    }


    public static void injectData(JEData data) {
        RuleEngine.injectData(data);
    }
    /*
     * stop running a project given a project id
     */
    public void stopRuleEngineProjectExecution(String projectId) throws RulesNotFiredException, RuleBuildFailedException, ProjectAlreadyRunningException {
        RuleEngine.stopRuleExecution(projectId);
    }


}
