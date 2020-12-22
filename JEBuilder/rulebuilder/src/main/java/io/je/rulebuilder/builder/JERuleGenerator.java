package io.je.rulebuilder.builder;

import java.util.List;
import java.util.Map;

import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.Condition;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.blocks.Block;
import io.je.rulebuilder.components.blocks.ConditionBlock;
import io.je.rulebuilder.components.blocks.LogicBlock;

public class JERuleGenerator {

	public static JERule buildJERule(UserDefinedRule userDefinedRule, String rootBlockId) {
		if (userDefinedRule.getRuleId() == null) {
			// throw error rule id
		}
		if (userDefinedRule.getProjectId() == null) {
			// thrw proj id error
		}
		// if no exec block, throw error

		JERule rule = new JERule(userDefinedRule.getRuleId(), userDefinedRule.getProjectId());
		if (userDefinedRule.getSalience() != null) {
			rule.setSalience(userDefinedRule.getSalience());
		}

		// add other parameters

		// create condition
	//	Condition Condition = generateCondition(block,composedRule.getBlocks());

		return null;
	}

	private static Condition generateCondition(ConditionBlock rootBlock, Map<String,Block> blocks) {
		Condition condition = new Condition(rootBlock);

		if (rootBlock.getInputBlocks().isEmpty()) {
			return condition;
		}

		else  {
			for (String inputBlockId : rootBlock.getInputBlocks()) {
				ConditionBlock inputBlock = (ConditionBlock) blocks.get(inputBlockId);
				if (inputBlock != null) {
					condition.addChild(generateCondition(inputBlock,blocks));

				}
			}
		} 
		return condition;
	}

}
