package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.RuleParameters;
import io.je.rulebuilder.components.ScriptedRule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.ExecutionBlock;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.logic.AndBlock;
import io.je.rulebuilder.components.blocks.logic.NotBlock;
import io.je.rulebuilder.components.blocks.logic.OrBlock;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.beans.JEResponse;
import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.log.JELogger;
import io.je.utilities.mapping.JERunnerRuleMapping;
import io.je.utilities.ruleutils.IdManager;
import org.apache.commons.text.StringSubstitutor;
import org.drools.template.ObjectDataCompiler;
import utils.date.DateUtils;
import utils.log.LogCategory;
import utils.log.LogSubModule;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Rule Builder class that builds .drl file from JERule instance
 */
public class RuleBuilder {

    public static final String LINE_SEPERATOR = "\n";
    public static final String AND_DROOLS = "and";
    public static final String AND_DROOLS_CONDITION = LINE_SEPERATOR + AND_DROOLS + LINE_SEPERATOR;

    public static final String OR_DROOLS = "or";
    public static final String OR_DROOLS_CONDITION = LINE_SEPERATOR + OR_DROOLS + LINE_SEPERATOR;

    public static final String NOT_DROOLS_PREFIX = "not (";

    public static final String NOT_DROOLS_PREFIX_CONDITION = LINE_SEPERATOR + NOT_DROOLS_PREFIX + LINE_SEPERATOR;

    public static final String NOT_DROOLS_SUFFIX = ")";

    public static final String NOT_DROOLS_SUFFIX_CONDITION = LINE_SEPERATOR + NOT_DROOLS_SUFFIX + LINE_SEPERATOR;

    //static boolean eliminateCombinatoryBehaviour = true;

    /* Private constructor */
    private RuleBuilder() {

    }

    // Avoid Drools error : Duplicate declaration for variable '... ' in the rule
    public static String getDroolsConditionWithoutRepeatedDeclarations(String condition) {
        String conditionWithoutDuplication = "";

        String[] splittedCondition = condition.split(LINE_SEPERATOR);

        // Outer loop
        outer_loop: for (int i = 0; i < splittedCondition.length; i++) {

            String line = splittedCondition[i];

            if (line.equals(AND_DROOLS) || line.equals(OR_DROOLS) || line.equals(NOT_DROOLS_PREFIX) || line.equals(NOT_DROOLS_SUFFIX)) {

                conditionWithoutDuplication += line + LINE_SEPERATOR;

                continue;
            }

            for (int j = 0; j < i; j++) {
                if (line.equals(splittedCondition[j])) {
                    continue outer_loop;
                }
            }

            conditionWithoutDuplication += line + LINE_SEPERATOR;

        }

        return conditionWithoutDuplication;
    }

    /*
     * Generate drl file from rules and saves them to the provided path
     */
    public static void buildRule(JERule jeRule, String buildPath, boolean compileOnly)
            throws RuleBuildFailedException, JERunnerErrorException {
        String rulePath = "";
        JELogger.control(JEMessages.BUILDING_RULE + " : " + jeRule.getJobEngineElementName(),
                LogCategory.DESIGN_MODE, jeRule.getJobEngineProjectID(),
                LogSubModule.RULE, jeRule.getJobEngineElementID());

        if (jeRule instanceof UserDefinedRule) {
            List<ScriptedRule> unitRules = scriptRule(((UserDefinedRule) jeRule));
            for (ScriptedRule scriptedRule : unitRules) {
                // generate drl
                rulePath = scriptedRule.generateDRL(buildPath);
                sendDRLToJeRunner(scriptedRule, rulePath, compileOnly);
            }
        }

        if (jeRule instanceof ScriptedRule) {
            rulePath = ((ScriptedRule) jeRule).generateDRL(buildPath);
            sendDRLToJeRunner(jeRule, rulePath, compileOnly);
        }

    }


    /*
     * send rule to JERunner
     */
    public static void sendDRLToJeRunner(JERule rule, String path, boolean compileOnly) throws RuleBuildFailedException, JERunnerErrorException {

        // compile rule

        HashMap<String, Object> ruleMap = new HashMap<>();

        ruleMap.put(JERunnerRuleMapping.PROJECT_ID, rule.getJobEngineProjectID());
        ruleMap.put(JERunnerRuleMapping.RULE_ID, rule.getJobEngineElementID());
        ruleMap.put(JERunnerRuleMapping.PROJECT_NAME, rule.getJobEngineProjectName());
        ruleMap.put(JERunnerRuleMapping.RULE_NAME, rule.getJobEngineElementName());

        ruleMap.put(JERunnerRuleMapping.RULE_PATH, path);
        // TODO: remove hard-coded rule format
        ruleMap.put(JERunnerRuleMapping.RULE_FORMAT, "DRL");
        ruleMap.put(JERunnerRuleMapping.RULE_TOPICS, rule.getTopics()
                .keySet());

        JELogger.debug(" [ project = " + rule.getJobEngineProjectName() + " ] [rule = " + rule.getJobEngineElementName() + "]"
                        + JEMessages.SENDNG_RULE_TO_RUNNER,
                LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                LogSubModule.RULE, rule.getJobEngineElementID());

        JELogger.debug(" Rule : " + ruleMap,
                LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                LogSubModule.RULE, rule.getJobEngineElementID());

        JEResponse jeRunnerResp = null;

        if (compileOnly) {

            jeRunnerResp = JERunnerAPIHandler.compileRule(ruleMap);

        } else {
            try {

                jeRunnerResp = JERunnerAPIHandler.updateRule(ruleMap);

            } catch (JERunnerErrorException e) {
                JELogger.logException(e);
                JELogger.error(" [ project = " + rule.getJobEngineProjectName() + " ] [rule = " + rule.getJobEngineElementName() + "]"
                                + JEMessages.RULE_BUILD_FAILED + " : " + e.getMessage(),
                        LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                        LogSubModule.RULE, rule.getJobEngineElementID());
                throw new RuleBuildFailedException(JEMessages.RULE_BUILD_FAILED + " : " + e.getMessage());
            }
        }

        if (jeRunnerResp == null || jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
            String response = jeRunnerResp == null ? JEMessages.JERUNNER_UNREACHABLE : jeRunnerResp.getMessage();
            JELogger.error("[project = " + rule.getJobEngineProjectName()
                            + "][rule =" + rule.getJobEngineElementName() + " ] : "
                            + JEMessages.RULE_BUILD_FAILED + response,
                    LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                    LogSubModule.RULE, rule.getJobEngineElementID());
            throw new RuleBuildFailedException(response);
        }

    }

    /*
     * Generate script rules
     */
    public static List<ScriptedRule> scriptRule(UserDefinedRule uRule) throws RuleBuildFailedException {

        uRule.getBlocks().init();

        List<String> subRules = new ArrayList<>();
        String duration = null;
        int scriptedRulesCounter = 0;
        String scriptedRuleId = "";
        List<ScriptedRule> scriptedRules = new ArrayList<>();
        Set<Block> rootBlocks = getRootBlocks(uRule);
        String subRulePrefix = IdManager.generateSubRulePrefix(uRule.getJobEngineElementID());

        for (Block root : rootBlocks) {
            uRule.getBlocks().resetAllBlocks();

            // TODO better management of font/back-end rule config de-synchronization
            if (!uRule.getRuleFrontConfig().contains("\"block_name\":\"" + root.getBlockName() + "\"")) {
                continue;
            }

            scriptedRuleId = subRulePrefix + uRule.getJobEngineElementName() + ++scriptedRulesCounter;

            String condition = "";
            String notCondition = "";
            String consequences = "";

            if (root instanceof ConditionBlock) {

                ConditionBlock conditionBlock = (ConditionBlock) root;

                // Do not change unless aware
                if (root instanceof OrBlock) {

                    OrBlock orBlock = (OrBlock) root;

                    // WARNING getDroolsConditionWithoutRepeatedDeclarations is called inside OrBlock
                    condition = orBlock.getExpression();

                    // WARNING getDroolsConditionWithoutRepeatedDeclarations is called inside OrBlock
                    notCondition = orBlock.getNotExpression();

                    consequences = orBlock.getConsequences();

                } else if (root instanceof AndBlock) {

                    AndBlock andBlock = (AndBlock) root;

                    condition = getDroolsConditionWithoutRepeatedDeclarations(andBlock.getExpression());

                    notCondition = getDroolsConditionWithoutRepeatedDeclarations(andBlock.getNotExpression());

                    consequences = andBlock.getConsequences();

                } else if (root instanceof NotBlock) {

                    NotBlock notBlock = (NotBlock) root;

                    condition = getDroolsConditionWithoutRepeatedDeclarations(notBlock.getExpression());

                    notCondition = getDroolsConditionWithoutRepeatedDeclarations(notBlock.getNotExpression());

                    consequences = notBlock.getConsequences();

                } else {

                    condition = getDroolsConditionWithoutRepeatedDeclarations(conditionBlock.getExpression());

                    // TODO check if need for more specific blocks cast
                    notCondition = " not ( \n" + condition.replaceAll("\n", AND_DROOLS_CONDITION) + "\n ) ";

                    consequences = conditionBlock.getConsequences();

                }

            } else {

                consequences = root.getExpression();

            }

            // Add time persistence
            String rootDuration = root.getPersistence();

            String script = generateScript(uRule.getRuleParameters(), scriptedRuleId, rootDuration, condition, consequences, notCondition);

            JELogger.debug(JEMessages.GENERATED_RULE + "\n" + script,
                    LogCategory.DESIGN_MODE, uRule.getJobEngineProjectID(),
                    LogSubModule.RULE, uRule.getJobEngineElementID());

            ScriptedRule scriptedRule = new ScriptedRule(uRule.getJobEngineProjectID(), scriptedRuleId, script,
                    uRule.getJobEngineElementName() + scriptedRulesCounter, uRule.getJobEngineProjectName());

            scriptedRule.setTopics(uRule.getTopics());

            scriptedRules.add(scriptedRule);

            subRules.add(scriptedRuleId);

            uRule.setSubRules(subRules);

        }

        return scriptedRules;
    }


    /* generate DRL for this rule */
    private static String generateScript(RuleParameters ruleParameters, String ruleId, String duration, String condition,
                                         String consequences, String notCondition)
            throws RuleBuildFailedException {

        try {

            // Set rule attributes
            Map<String, String> ruleTemplateAttributes = new HashMap<>();

            ruleTemplateAttributes.put("customImport", ConfigurationConstants.getJobEngineCustomImport());
            ruleTemplateAttributes.put("ruleName", ruleId);
            ruleTemplateAttributes.put("salience", ruleParameters.getSalience());
            ruleTemplateAttributes.put("cronExpression", ruleParameters.getTimer());
            ruleTemplateAttributes.put("enabled", ruleParameters.getEnabled());

            if (ruleParameters.getDateEffective() != null && !ruleParameters.getDateEffective()
                    .isEmpty()) {
                LocalDateTime date = LocalDateTime.ofInstant(Instant.parse(ruleParameters.getDateEffective()), ZoneId.systemDefault());
                ruleTemplateAttributes.put("dateEffective", "\"" + DateUtils.formatDate(date, ConfigurationConstants.DROOLS_DATE_FORMAT) + "\"");
            }
            if (ruleParameters.getDateExpires() != null && !ruleParameters.getDateExpires()
                    .isEmpty()) {
                LocalDateTime date = LocalDateTime.ofInstant(Instant.parse(ruleParameters.getDateExpires()), ZoneId.systemDefault());
                ruleTemplateAttributes.put("dateExpires", "\"" + DateUtils.formatDate(date, ConfigurationConstants.DROOLS_DATE_FORMAT) + "\"");
            }

            ruleTemplateAttributes.put("condition", condition);

            ruleTemplateAttributes.put("consequence", consequences);

            ruleTemplateAttributes.put("notCondition", notCondition);

            // Duration replaced by Persistence
            long persistence = 0;
            if (duration != null) {
                if (duration.endsWith("s")) {
                    persistence = Long.parseLong(duration.substring(0, duration.length() - 1)) * 1000;
                } else if (duration.endsWith("m")) {
                    persistence = Long.parseLong(duration.substring(0, duration.length() - 1)) * 1000 * 60;
                } else if (duration.endsWith("h")) {
                    persistence = Long.parseLong(duration.substring(0, duration.length() - 1)) * 1000 * 3600;
                }
            }

            ruleTemplateAttributes.put("persistence", "" + persistence);

            // Avoid evaluation of Reset Persistence Rule if persistence not > 0
            Boolean resetPersistenceEnabled = persistence > 0;

            InputStream inputStream = RuleBuilder.class.getClassLoader().
                    getResourceAsStream("ResetPersistenceRuleTemplate.drl");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String resetPersistenceRule = reader.lines()
                    .collect(Collectors.joining("\n"));

            inputStream = RuleBuilder.class.getClassLoader().
                    getResourceAsStream("RuleTemplate.drl");

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String drlTemplate = reader.lines()
                    .collect(Collectors.joining("\n"));

            reader.close();

            // TODO enhance Logic OR code (do not change position unless aware)
            drlTemplate = drlTemplate.replace("@{condition}", condition);

            // TODO enhance persistence code (do not change position unless aware)
            if (resetPersistenceEnabled) {
                drlTemplate = drlTemplate.replace("@{resetPersistenceRule}", resetPersistenceRule);
            } else {
                ruleTemplateAttributes.put("resetPersistenceRule", "");
            }

            //System.err.println("drl template : \n" + drlTemplate);

            ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();

            String drlCompiled = objectDataCompiler.compile(Arrays.asList(ruleTemplateAttributes),
                    new ByteArrayInputStream(drlTemplate.getBytes()));

            //System.err.println("drl compiled : \n" + drlTemplate);

            String drlContent = StringSubstitutor.replace(drlCompiled, ruleTemplateAttributes);

            //System.err.println("drl after replace : \n" + drlContent);

            return drlContent;

        } catch (Exception exception) {

            JELogger.logException(exception);

            throw new RuleBuildFailedException(JEMessages.RULE_BUILD_FAILED + " : " + exception.getMessage());

        }

    }

    public static Set<Block> getRootBlocks(UserDefinedRule uRule) throws RuleBuildFailedException {
        Set<Block> roots = new HashSet<>();

        // Number of execution blocks
        int executionBlockCounter = 0;

        if (uRule.getBlocks() != null && uRule.getBlocks().getAll() != null) {

            // Get root blocks
            for (Block executionBlock : uRule.getBlocks().getAll()) {

                if (executionBlock instanceof ExecutionBlock) {

                    executionBlockCounter++;

                    // If exec block has no input block link, it's a root FIXME is it true?
                    if (executionBlock.getInputBlockLinks() == null || executionBlock.getInputBlockLinks().isEmpty()) {

                        roots.add(executionBlock);

                    } else {

                        for (var rootInputBlockLink : executionBlock.getInputBlockLinks()) {

                            if (rootInputBlockLink != null && rootInputBlockLink.getBlock() != null) {

                                Block block = uRule.getBlocks().getBlock(rootInputBlockLink.getBlock().getJobEngineElementID());

                                if (block != null) {

                                    roots.add(block);

                                }

                                if (block.getInputBlockLinks() != null) {

                                    for (var b : block.getInputBlockLinks()) {

                                        if (b.getBlock() instanceof PersistableBlock) {
                                            ((PersistableBlock) b.getBlock()).setTimePersistenceValue(((PersistableBlock) rootInputBlockLink.getBlock()).getTimePersistenceValue());
                                            ((PersistableBlock) b.getBlock()).setTimePersistenceUnit(((PersistableBlock) rootInputBlockLink.getBlock()).getTimePersistenceUnit());
                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

        // If this rule has no execution block, then it is not valid.
        if (executionBlockCounter == 0) {
            JELogger.error("[project = " + uRule.getJobEngineProjectName() + "][rule = " + uRule.getJobEngineElementName() + "] " + JEMessages.NO_EXECUTION_BLOCK,
                    LogCategory.DESIGN_MODE, uRule.getJobEngineProjectID(),
                    LogSubModule.RULE, uRule.getJobEngineElementID());
            throw new RuleBuildFailedException(JEMessages.NO_EXECUTION_BLOCK);
        }

        return roots;
    }


    public static void buildAndRun(JERule jeRule, String configurationPath) {
        // TODO Auto-generated method stub

    }

}