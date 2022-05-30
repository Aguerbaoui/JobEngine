package io.je.rulebuilder.builder;

import io.je.rulebuilder.components.*;
import io.je.rulebuilder.components.blocks.*;
import io.je.rulebuilder.components.blocks.execution.LinkedSetterBlock;
import io.je.rulebuilder.components.blocks.execution.SetterBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.rulebuilder.components.blocks.logic.JoinBlock;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/*
 * Rule Builder class that builds .drl file from JERule instance
 */
public class RuleBuilder {

    static boolean eliminateCombinatoryBehaviour = true;

    /* private constructor */
    private RuleBuilder() {

    }

    /*
     * generate drl file from rules and saves them to the provided path
     */
    public static void buildRule(JERule jeRule, String buildPath, boolean compileOnly)
            throws RuleBuildFailedException, JERunnerErrorException {
        String rulePath = "";
        JELogger.control(JEMessages.BUILDING_RULE + " : " + jeRule.getJobEngineElementName(),
                LogCategory.DESIGN_MODE, jeRule.getJobEngineProjectID(),
                LogSubModule.RULE, jeRule.getJobEngineElementID());
        if (jeRule instanceof UserDefinedRule) {

            List<ScriptedRule> unitRules = jeRule.isCompiled() ? ((UserDefinedRule) jeRule).getUnitRules() : scriptRule(((UserDefinedRule) jeRule));
            for (ScriptedRule rule : unitRules) {
                // generate drl
                rulePath = rule.generateDRL(buildPath);
                sendDRLToJeRunner(rule, rulePath, compileOnly);
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
        ruleMap.put(JERunnerRuleMapping.PROJECT_NAME, rule.getJobEngineProjectName());
        ruleMap.put(JERunnerRuleMapping.PATH, path);
        ruleMap.put(JERunnerRuleMapping.RULE_ID, rule.getJobEngineElementID());
        ruleMap.put(JERunnerRuleMapping.RULE_NAME, rule.getJobEngineElementName());
        // TODO: remove hard-coded rule format
        ruleMap.put(JERunnerRuleMapping.FORMAT, "DRL");
        ruleMap.put(JERunnerRuleMapping.TOPICS, rule.getTopics()
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
                JELogger.error(" [ project = " + rule.getJobEngineProjectName() + " ] [rule = " + rule.getJobEngineElementName() + "]"
                                + JEMessages.RULE_BUILD_FAILED,
                        LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                        LogSubModule.RULE, rule.getJobEngineElementID());
                throw new RuleBuildFailedException(JEMessages.RULE_BUILD_FAILED + ": " + e.getMessage());
            }
        }


        if (jeRunnerResp == null || jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
            String response = jeRunnerResp == null ? JEMessages.JERUNNER_UNREACHABLE : jeRunnerResp.getMessage();
            JELogger.error("[project = " + rule.getJobEngineProjectName() + "][rule =" + rule.getJobEngineElementName() + " ]" + JEMessages.RULE_BUILD_FAILED + response,
                    LogCategory.DESIGN_MODE, rule.getJobEngineProjectID(),
                    LogSubModule.RULE, rule.getJobEngineElementID());
            throw new RuleBuildFailedException(response);
        }


    }

    /*
     * generate script rules
     */
    public static List<ScriptedRule> scriptRule(UserDefinedRule uRule) throws RuleBuildFailedException {
        uRule.getBlocks()
                .init();
        List<String> subRules = new ArrayList<>();
        String duration = null;
        int scriptedRulesCounter = 0;
        String scriptedRuleid = "";
        uRule.setUnitRules(new ArrayList<>());
        Set<Block> rootBlocks = getRootBlocks(uRule);
        String subRulePrefix = IdManager.generateSubRulePrefix(uRule.getJobEngineElementID());
        for (Block root : rootBlocks) {
            uRule.getBlocks()
                    .resetAllBlocks();
            scriptedRuleid = subRulePrefix + uRule.getJobEngineElementName() + ++scriptedRulesCounter;
            GenericBlockSummary allGenericBlocksByClassId = new GenericBlockSummary(scriptedRuleid);
            if (eliminateCombinatoryBehaviour && !(root instanceof JoinBlock)) {
                allGenericBlocksByClassId = getAllGenericGetterBlocksByClassId(root, allGenericBlocksByClassId);
                allGenericBlocksByClassId = getAllSetterBlocksByClassId(root, allGenericBlocksByClassId);
                eliminateCombinatoryBehaviour(allGenericBlocksByClassId, root.getInitialJoinBlock());
            }
            String condition = "";
            if (root instanceof ConditionBlock) {
                condition = root.getExpression();

            }

            String consequences = "";
            if (root instanceof ConditionBlock) {
                consequences = ((ConditionBlock) root).getConsequences();


            } else {
                consequences = root.getExpression();
            }
            // add time persistence
            String rootDuration = root.getPersistence();
            String script = generateScript(uRule.getRuleParameters(), scriptedRuleid, rootDuration, condition, consequences);
            JELogger.debug(JEMessages.GENERATED_RULE + script,
                    LogCategory.DESIGN_MODE, uRule.getJobEngineProjectID(),
                    LogSubModule.RULE, uRule.getJobEngineElementID());
            ScriptedRule rule = new ScriptedRule(uRule.getJobEngineProjectID(), scriptedRuleid, script,
                    uRule.getJobEngineElementName() + scriptedRulesCounter, uRule.getJobEngineProjectName());
            rule.setTopics(uRule.getTopics());
            uRule.getUnitRules()
                    .add(rule);
            subRules.add(scriptedRuleid);
            uRule.setSubRules(subRules);
            if (eliminateCombinatoryBehaviour) {
                resetJoinIds(allGenericBlocksByClassId);
            }
        }
        return uRule.getUnitRules();
    }

    private static GenericBlockSummary eliminateCombinatoryBehaviour(GenericBlockSummary allGenericBlocks, String primeJoinId) {

        if (allGenericBlocks.getNumberOfClasses() == 1 && allGenericBlocks.allBlocksAreGeneric()) {
            GenericBlockSet set = allGenericBlocks.getSingleBlockSet();
            if (!set.isAllBlocksAreSetters()) {
                Optional<Block> b = set.getIdentifier(primeJoinId);
                if (b.isPresent()) {
                    set.getValue()
                            .remove(b.get());
                    set.getBlocks()
                            .stream()
                            .forEach(bl -> bl.addSpecificInstance(primeJoinId + ".getJobEngineElementID()"));
                    set.getValue()
                            .add(b.get());
                }
            }
        }


        return allGenericBlocks;

    }

    private static void resetJoinIds(GenericBlockSummary allGenericBlocks) {

        if (allGenericBlocks.getNumberOfClasses() == 1 && allGenericBlocks.allBlocksAreGeneric()) {
            GenericBlockSet set = allGenericBlocks.getSingleBlockSet();
            if (!set.isAllBlocksAreSetters()) {
                set.getBlocks()
                        .stream()
                        .forEach(bl -> bl.removeSpecificInstance());
            }
        }

    }

    /*
     * Group all the getter blocks by Id
     */
    private static GenericBlockSummary getAllGenericGetterBlocksByClassId(Block root,
                                                                          GenericBlockSummary allAttributeBlocks) {

        for (Block input : root.getInputBlocks()) {
            if (input instanceof AttributeGetterBlock) {
                AttributeGetterBlock getter = (AttributeGetterBlock) input;
                allAttributeBlocks.addGetterBlock(getter);
            } else {
                allAttributeBlocks = getAllGenericGetterBlocksByClassId(input, allAttributeBlocks);
            }
        }
        return allAttributeBlocks;
    }

    /*
     * Group all the setter blocks by Id
     */
    private static GenericBlockSummary getAllSetterBlocksByClassId(Block root,
                                                                   GenericBlockSummary allAttributeBlocks) {

        for (Block output : root.getOutputBlocks()) {
            if (output instanceof SetterBlock) {
                allAttributeBlocks.addSetterBlock((SetterBlock) output);
            }
            if (output instanceof LinkedSetterBlock) {
                allAttributeBlocks.addSetterBlock((LinkedSetterBlock) output);
            }
        }
        return allAttributeBlocks;
    }

    /* generate DRL for this rule */

    private static String generateScript(RuleParameters ruleParameters, String ruleId, String duration, String condition, String consequences)
            throws RuleBuildFailedException {

        // set rule attributes
        Map<String, String> ruleTemplateAttributes = new HashMap<>();
        ruleTemplateAttributes.put("customImport", ConfigurationConstants.getJobEngineCustomImport());
        ruleTemplateAttributes.put("ruleName", ruleId);
        ruleTemplateAttributes.put("salience", ruleParameters.getSalience());
        ruleTemplateAttributes.put("cronExpression", ruleParameters.getTimer());
        ruleTemplateAttributes.put("enabled", ruleParameters.getEnabled());
        ruleTemplateAttributes.put("condition", condition);
        ruleTemplateAttributes.put("consequence", consequences);
        ruleTemplateAttributes.put("duration", duration);
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

        ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
        String ruleContent = "";
        try {
            ruleContent = objectDataCompiler.compile(Arrays.asList(ruleTemplateAttributes),
                    RuleBuilder.class.getClassLoader()
                            .getResourceAsStream("RuleTemplate.drl"));
        } catch (Exception e) {
            throw new RuleBuildFailedException(JEMessages.RULE_BUILD_FAILED + e.getMessage());
        }

        String content = StringSubstitutor.replace(ruleContent, ruleTemplateAttributes);
        return content;

    }

    public static Set<Block> getRootBlocks(UserDefinedRule uRule) throws RuleBuildFailedException {
        Set<Block> roots = new HashSet<>();

        // number of execution blocks
        int executionBlockCounter = 0;
        // get root blocks
        for (Block ruleBlock : uRule.getBlocks()
                .getAll()) {
            if (ruleBlock instanceof ExecutionBlock) {
                executionBlockCounter++;
                for (Block rootBlock : ruleBlock.getInputBlocks()) {
                    if (rootBlock != null)
                    // HA 05/25/2022 removed and add explicit or     if(!(rootBlock instanceof OrBlock))
                    {
                        roots.add(uRule.getBlocks()
                                .getBlock(rootBlock.getJobEngineElementID()));
                        for (Block b : uRule.getBlocks()
                                .getBlock(rootBlock.getJobEngineElementID())
                                .getInputBlocks()) {
                            if (b instanceof PersistableBlock) {
                                ((PersistableBlock) b).setTimePersistenceValue(((PersistableBlock) rootBlock).getTimePersistenceValue());
                                ((PersistableBlock) b).setTimePersistenceUnit(((PersistableBlock) rootBlock).getTimePersistenceUnit());
                            }

                        }
                    }

                }

                // if exec block has no root, it's a root
                if (ruleBlock.getInputBlocks()
                        .isEmpty()) {
                    roots.add(ruleBlock);
                }

            }
        }
        // if this rule has no execution block, then it is not valid.
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