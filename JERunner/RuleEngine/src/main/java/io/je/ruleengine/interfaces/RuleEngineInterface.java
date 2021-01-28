package io.je.ruleengine.interfaces;

import io.je.ruleengine.models.Rule;
import io.je.utilities.runtimeobject.JEObject;

import java.util.List;

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

    public boolean fireRules(String projectId, List<Rule> rules, boolean removePreviouslyAddedRules);

    public boolean stopRuleExecution(String projectId);


    //FactManagement
    public boolean insertFact(JEObject fact);

    public boolean retractFact(JEObject fact);

    public boolean updateFact(JEObject fact);


}
