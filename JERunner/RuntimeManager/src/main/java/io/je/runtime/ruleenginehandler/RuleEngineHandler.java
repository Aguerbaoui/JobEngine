package io.je.runtime.ruleenginehandler;


import io.je.ruleengine.impl.RuleEngine;
import io.je.ruleengine.models.Rule;
import io.je.runtime.models.RunnerRuleModel;
import io.je.utilities.beans.JEEvent;
import io.je.utilities.beans.JEVariable;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.exceptions.*;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.util.HashSet;
import java.util.Set;

/*
 * class responsible for Rule Engine calls
 */
public class RuleEngineHandler {


    private RuleEngineHandler() {

    }


    private static String verifyRuleIsValid(RunnerRuleModel runnerRuleModel) throws RuleFormatNotValidException {
        JELogger.debug(JEMessages.VALIDATING_RULE,
                LogCategory.RUNTIME, runnerRuleModel.getProjectId(),
                LogSubModule.RULE, runnerRuleModel.getRuleId());

        String errorMsg = null;
        if (runnerRuleModel.getRuleId() == null || runnerRuleModel.getRuleId().isEmpty()) {

            errorMsg = JEMessages.ID_NOT_FOUND;
            throw new RuleFormatNotValidException(errorMsg);
        }
        if (runnerRuleModel.getRulePath() == null || runnerRuleModel.getRulePath().isEmpty()) {
            errorMsg = JEMessages.RULE_FILE_NOT_FOUND;
            throw new RuleFormatNotValidException(errorMsg);
        }
        if (runnerRuleModel.getProjectId() == null || runnerRuleModel.getProjectId().isEmpty()) {
            errorMsg = JEMessages.RULE_PROJECT_ID_NULL;
            throw new RuleFormatNotValidException(errorMsg);
        }

        return errorMsg;
    }

    /*
     * add rule to rule engine
     */

    public static void addRule(RunnerRuleModel runnerRuleModel) throws RuleAlreadyExistsException, RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

        verifyRuleIsValid(runnerRuleModel);

        Rule rule = new Rule(runnerRuleModel.getRuleId(), runnerRuleModel.getProjectId(), runnerRuleModel.getRuleName(),
                runnerRuleModel.getProjectName(), runnerRuleModel.getFormat(), runnerRuleModel.getRulePath());

        RuleEngine.addRule(rule);

    }

    /*
     * update rule in rule engine
     */
    public static void updateRule(RunnerRuleModel runnerRuleModel) throws RuleCompilationException, JEFileNotFoundException, RuleFormatNotValidException {

        verifyRuleIsValid(runnerRuleModel);

        // FIXME runnerRuleModel.getRuleName() null
        Rule rule = new Rule(runnerRuleModel.getRuleId(), runnerRuleModel.getProjectId(), runnerRuleModel.getRuleName(),
                runnerRuleModel.getProjectName(), runnerRuleModel.getFormat(), runnerRuleModel.getRulePath());

        rule.setTopics(runnerRuleModel.getTopics());

        RuleEngine.updateRule(rule);

    }


    /*
     * Start running a project rule engine given a project id
     */
    public static void startProjectRuleEngine(String projectId) throws RulesNotFiredException, RuleBuildFailedException {

        RuleEngine.start(projectId);

    }


    /*
     * Stop running a project given a project id
     */
    public static void stopProjectRuleEngine(String projectId) {

        RuleEngine.stop(projectId);

    }


    /*
     * build project
     */
    public static void buildProject(String projectId) throws RuleBuildFailedException {

        RuleEngine.buildProject(projectId);

    }

    /*
     * compile rule
     */
    public static void compileRule(RunnerRuleModel runnerRuleModel) throws RuleFormatNotValidException, RuleCompilationException, JEFileNotFoundException {

        verifyRuleIsValid(runnerRuleModel);

        Rule rule = new Rule(runnerRuleModel.getRuleId(), runnerRuleModel.getProjectId(), runnerRuleModel.getRuleName(),
                runnerRuleModel.getProjectName(), runnerRuleModel.getFormat(), runnerRuleModel.getRulePath());

        RuleEngine.compileRule(rule);

    }

    public static void deleteRule(String projectId, String ruleId) throws DeleteRuleException {
        RuleEngine.deleteRule(projectId, ruleId);

    }

    public static void addEvent(JEEvent event) {
        RuleEngine.insertFact(event.getJobEngineProjectID(), event);

    }

    public static void deleteProjectRules(String projectId) {
        JELogger.debug("[project id = " + projectId + "] " + JEMessages.DELETING_RULES,
                LogCategory.RUNTIME, projectId,
                LogSubModule.RULE, null);
        RuleEngine.deleteProjectRules(projectId);
    }


    public static Set<String> getRuleTopics(String projectId, String ruleId) {
        Rule rule = RuleEngine.getRule(projectId, ruleId);
        if (rule != null) {
            return rule.getTopics();
        }
        return new HashSet<>();
    }


    public static void addVariable(JEVariable variable) {
        RuleEngine.insertFact(variable.getJobEngineProjectID(), variable);

    }

    public static void deleteVariable(String projectId, String id) {
        RuleEngine.deleteFact(projectId, id);

    }

    public static void deleteEvent(String projectId, String id) {
        RuleEngine.deleteFact(projectId, id);

    }


    public static void clearRuleTopics(String projectId, String ruleId) {
        Rule rule = RuleEngine.getRule(projectId, ruleId);
        if (rule != null) {
            rule.getTopics().clear();
        }

    }


    public static void reloadContainers() {
        RuleEngine.reloadContainers();

    }

}
