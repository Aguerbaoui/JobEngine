package io.je.rulebuilder.components;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import io.je.utilities.config.ConfigurationConstants;
import io.je.utilities.constants.JEMessages;

import org.drools.template.ObjectDataCompiler;

import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.PersistableBlock;
import io.je.rulebuilder.components.blocks.getter.AttributeGetterBlock;
import io.je.utilities.exceptions.AddRuleBlockException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.ruleutils.RuleIdManager;

/*
 * Rules defined graphically by the user.
 * One UserDefinedRule can be equivalents to multiple drl rules 
 * Each Job engine rule is defined by a root block ( a logic or comparison block that precedes and execution sequence)
 */
public class UserDefinedRule extends JERule {

	/*
	 * rule attributes
	 */
	RuleParameters ruleParameters;

	/*
	 * Map of all the blocks that define this rule
	 */
	BlockManager blocks = new BlockManager();

	/*
	 * * One UserDefinedRule can be equivalents to multiple drl rules List of all
	 * the subRules
	 */

	List<String> subRules = new ArrayList<String>();

	public UserDefinedRule() {
		ruleParameters = new RuleParameters();
	}

	/*
	 * generate script rules
	 */
	public List<ScriptedRule> scriptRule() throws RuleBuildFailedException {
		blocks.init();
		subRules = new ArrayList<String>();
		String duration = null;
		int scriptedRulesCounter = 0;
		String scriptedRuleid = "";
		List<ScriptedRule> scriptedRules = new ArrayList<>();
		Set<Block> rootBlocks = blocks.getRootBlocks();
		String subRulePrefix = RuleIdManager.generateSubRulePrefix(jobEngineElementID);
		for (Block root : rootBlocks) {
			scriptedRuleid = subRulePrefix + ruleName + ++scriptedRulesCounter;
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

			String script = generateScript(scriptedRuleid, duration, condition, consequences);
			JELogger.debug(getClass(), "generated DRL : \n" + script);
			ScriptedRule rule = new ScriptedRule(jobEngineProjectID, scriptedRuleid, script,
					ruleName + scriptedRulesCounter);
			rule.setTopics(getTopics());
			scriptedRules.add(rule);
			subRules.add(scriptedRuleid);
		}
		return scriptedRules;
	}

	/* generate DRL for this rule */

	private String generateScript(String ruleId, String duration, String condition, String consequences)
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
			ruleTemplateAttributes.put("dateEffective", "\"" + ruleParameters.getDateEffective() + "\"");
		}
		if (ruleParameters.getDateExpires() != null && !ruleParameters.getDateExpires().isEmpty()) {
			ruleTemplateAttributes.put("dateExpires", "\"" + ruleParameters.getDateExpires() + "\"");
		}

		ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();
		String ruleContent = "";
		try {
			ruleContent = objectDataCompiler.compile(Arrays.asList(ruleTemplateAttributes),
					new FileInputStream(ConfigurationConstants.RULE_TEMPLATE_PATH));
		} catch (Exception e) {
			throw new RuleBuildFailedException(JEMessages.RULE_BUILD_FAILED + e.getMessage());
		}
		return ruleContent;

	}

	/*
	 * add a block to this user defined rule
	 */
	public void addBlock(Block block) {
		blocks.addBlock(block);
		isBuilt = false;
	}

	/*
	 * update a block in this user defined rule
	 */
	public void updateBlock(Block block) {
		blocks.updateBlock(block);
		isBuilt = false;

	}

	/*
	 * delete a block in this user defined rule
	 */
	public void deleteBlock(String blockId) {
		if (blocks.getBlock(blockId) instanceof AttributeGetterBlock) {
			AttributeGetterBlock getter = (AttributeGetterBlock) blocks.getBlock(blockId);
			removeTopic(getter.getClassId());
		}
		blocks.deleteBlock(blockId);
		isBuilt = false;

	}

	public RuleParameters getRuleParameters() {
		return ruleParameters;
	}

	public void setRuleParameters(RuleParameters ruleParameters) {
		this.ruleParameters = ruleParameters;
		isBuilt = false;

	}

	public BlockManager getBlocks() {
		return blocks;
	}

	public Enumeration<String> getAllBlockIds()
	{
		return blocks.getAllBlockIds();
	}
	
	public void setBlocks(BlockManager blocks) {
		this.blocks = blocks;
	}

	public List<String> getSubRules() {
		return subRules;
	}

	public void setSubRules(List<String> subRules) {
		this.subRules = subRules;
	}

	public boolean containsBlock(String blockId) {
		return blocks.containsBlock(blockId);
	}

}
