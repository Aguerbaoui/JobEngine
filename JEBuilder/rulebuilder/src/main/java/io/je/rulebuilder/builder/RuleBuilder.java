package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.RuleParameters;
import io.je.rulebuilder.components.ScriptedRule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.config.Utility;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.logger.LogCategory;
import io.je.utilities.logger.LogSubModule;
import io.je.utilities.mapping.JERunnerRuleMapping;
import io.je.utilities.network.JEResponse;
import io.je.utilities.ruleutils.RuleIdManager;
import io.je.utilities.time.JEDate;
import org.drools.template.ObjectDataCompiler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

/*
 * Rule Builder class that builds .drl file from JERule instance
 */
public class RuleBuilder {

    /* private constructor */
    private RuleBuilder() {

    }

    /*
     * generate drl file from rules and saves them to the provided path
     */
    public static void buildRule(JERule jeRule, String buildPath)
            throws RuleBuildFailedException, JERunnerErrorException, IOException, InterruptedException, ExecutionException {
        String rulePath = "";
        JELogger.debug(JEMessages.BUILDING_RULE + " : id = " + jeRule.getJobEngineElementID(),
                LogCategory.DESIGN_MODE, jeRule.getJobEngineProjectID(),
                LogSubModule.RULE, jeRule.getJobEngineElementID());
        if (jeRule instanceof UserDefinedRule) {
            List<ScriptedRule> unitRules = scriptRule(((UserDefinedRule) jeRule));
            for (ScriptedRule rule : unitRules) {
                // generate drl
                rulePath = rule.generateDRL(buildPath);
                sendDRLToJeRunner(rule, rulePath);
            }
        }
        if (jeRule instanceof ScriptedRule) {
            rulePath = ((ScriptedRule) jeRule).generateDRL(buildPath);
            sendDRLToJeRunner(jeRule, rulePath);
        }

    }


    /*
     * send rule to JERunner
     */
    public static void sendDRLToJeRunner(JERule rule, String path) throws JERunnerErrorException, RuleBuildFailedException, IOException, InterruptedException, ExecutionException {


        // compile rule

        HashMap<String, Object> ruleMap = new HashMap<>();
        ruleMap.put(JERunnerRuleMapping.PROJECT_ID, rule.getJobEngineProjectID());
        ruleMap.put(JERunnerRuleMapping.PATH, path);
        ruleMap.put(JERunnerRuleMapping.RULE_ID, rule.getJobEngineElementID());

        // TODO: remove hard-coded rule format
        ruleMap.put(JERunnerRuleMapping.FORMAT, "DRL");
        ruleMap.put(JERunnerRuleMapping.TOPICS, rule.getTopics().keySet());

        JELogger.debug(" [ project id = " + rule.getJobEngineProjectID() + " ] [rule id = " + rule.getJobEngineElementID() + "]"
                        + JEMessages.SENDNG_RULE_TO_RUNNER,
                LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                LogSubModule.RULE, rule.getJobEngineElementID());
        JELogger.debug(" Rule : " + ruleMap,
                LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                LogSubModule.RULE, rule.getJobEngineElementID());
        JEResponse jeRunnerResp = null;
        jeRunnerResp = JERunnerAPIHandler.updateRule(ruleMap);

        if (jeRunnerResp == null || jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
			JELogger.error("[rule id =" + rule.getRuleName() + " ]" + JEMessages.RULE_BUILD_FAILED + jeRunnerResp.getMessage(),
					LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
					LogSubModule.RULE, rule.getJobEngineElementID());
            throw new RuleBuildFailedException(jeRunnerResp.getMessage());
        }
        

    }

    /*
     * generate script rules
     */
    public static List<ScriptedRule> scriptRule(UserDefinedRule uRule) throws RuleBuildFailedException {
        uRule.getBlocks().init();
        List<String> subRules = new ArrayList<String>();
        String duration = null;
        int scriptedRulesCounter = 0;
        String scriptedRuleid = "";
        List<ScriptedRule> scriptedRules = new ArrayList<>();
        Set<Block> rootBlocks = getRootBlocks(uRule);
        String subRulePrefix = RuleIdManager.generateSubRulePrefix(uRule.getJobEngineElementID());
        for (Block root : rootBlocks) {
            scriptedRuleid = subRulePrefix + uRule.getRuleName() + ++scriptedRulesCounter;
            String condition = "";
            if (root instanceof ConditionBlock) {
                condition = root.getExpression();

            }

            String consequences = "";
            if (root instanceof ConditionBlock) {
                consequences = ((ConditionBlock) root).getConsequences();
                if (root instanceof PersistableBlock) {
                    duration = ((PersistableBlock) root).getPersistanceExpression();
                }

            } else {
                consequences = root.getExpression();
            }
            // add time persistence

            String script = generateScript(uRule.getRuleParameters(), scriptedRuleid, duration, condition, consequences);
			JELogger.debug("generated DRL : \n" + script,
					LogCategory.DESIGN_MODE, uRule.getJobEngineProjectID(),
					LogSubModule.RULE, uRule.getJobEngineElementID());
            ScriptedRule rule = new ScriptedRule(uRule.getJobEngineProjectID(), scriptedRuleid, script,
                    uRule.getRuleName() + scriptedRulesCounter);
            rule.setTopics(uRule.getTopics());
            scriptedRules.add(rule);
            subRules.add(scriptedRuleid);
            uRule.setSubRules(subRules);
        }
        return scriptedRules;
    }

    /* generate DRL for this rule */

    private static String generateScript(RuleParameters ruleParameters, String ruleId, String duration, String condition, String consequences)
            throws RuleBuildFailedException {

        // set rule attributes
        Map<String, String> ruleTemplateAttributes = new HashMap<>();
        ruleTemplateAttributes.put("ruleName", ruleId);
        ruleTemplateAttributes.put("salience", ruleParameters.getSalience());
        ruleTemplateAttributes.put("cronExpression", ruleParameters.getTimer());
        ruleTemplateAttributes.put("enabled", ruleParameters.getEnabled());
        ruleTemplateAttributes.put("condition", condition);
        ruleTemplateAttributes.put("consequence", consequences);
        ruleTemplateAttributes.put("duration", duration);
        if (ruleParameters.getDateEffective() != null && !ruleParameters.getDateEffective().isEmpty()) {
            LocalDateTime date = LocalDateTime.parse(ruleParameters.getDateEffective(), DateTimeFormatter.ISO_DATE_TIME);

            ruleTemplateAttributes.put("dateEffective", "\"" + JEDate.formatDate(date, Utility.getSiothConfig().getDateFormat()) + "\"");
        }
        if (ruleParameters.getDateExpires() != null && !ruleParameters.getDateExpires().isEmpty()) {
            LocalDateTime date = LocalDateTime.parse(ruleParameters.getDateEffective(), DateTimeFormatter.ISO_DATE_TIME);
            ruleTemplateAttributes.put("dateExpires", "\"" + JEDate.formatDate(date, Utility.getSiothConfig().getDateFormat()) + "\"");
        }

        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        String ruleContent = "";
        try {
            ruleContent = objectDataCompiler.compile(Arrays.asList(ruleTemplateAttributes),
                    RuleBuilder.class.getClassLoader().getResourceAsStream("RuleTemplate.drl"));
        } catch (Exception e) {
            throw new RuleBuildFailedException(JEMessages.RULE_BUILD_FAILED + e.getMessage());
        }
        return ruleContent;

    }

    public static Set<Block> getRootBlocks(UserDefinedRule uRule) throws RuleBuildFailedException {
        Set<Block> roots = new HashSet<>();

        // number of execution blocks
        int executionBlockCounter = 0;
        // get root blocks
        for (Block ruleBlock : uRule.getBlocks().getAll()) {
            if (ruleBlock instanceof ExecutionBlock) {
                executionBlockCounter++;
                for (Block rootBlock : ruleBlock.getInputBlocks()) {
                    if (rootBlock != null) {
                        roots.add(uRule.getBlocks().getBlock(rootBlock.getJobEngineElementID()));
                    }

                }

                // if exec block has no root, it's a root
                if (ruleBlock.getInputBlocks().isEmpty()) {
                    roots.add(ruleBlock);
                }

            }
        }
        // if this rule has no execution block, then it is not valid.
        if (executionBlockCounter == 0) {
			JELogger.error(JEMessages.NO_EXECUTION_BLOCK,
					LogCategory.DESIGN_MODE, uRule.getJobEngineProjectID(),
					LogSubModule.RULE, uRule.getJobEngineElementID());
            throw new RuleBuildFailedException(JEMessages.NO_EXECUTION_BLOCK);
        }

        return roots;
    }

}