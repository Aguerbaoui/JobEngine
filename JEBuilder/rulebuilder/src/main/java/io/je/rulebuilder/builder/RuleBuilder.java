package io.je.rulebuilder.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.config.JERunnerRuleMapping;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.network.JEResponse;

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
	public static void buildRule(UserDefinedRule userDefinedRule, String buildPath)
			throws RuleBuildFailedException, JERunnerUnreachableException, IOException {
		List<JERule> unitRules = userDefinedRule.build();
		boolean ruleIsBuilt = userDefinedRule.isBuilt();
		for (JERule rule : unitRules) {

			String rulePath = "";

			// generate drl
			rulePath = rule.generateDRL(buildPath);

			// compile rule

			HashMap<String, String> ruleMap = new HashMap<>();
			ruleMap.put(JERunnerRuleMapping.PROJECT_ID, rule.getJobEngineProjectID());
			ruleMap.put(JERunnerRuleMapping.PATH, rulePath);
			ruleMap.put(JERunnerRuleMapping.RULE_ID, rule.getJobEngineElementID());

			// TODO: remove hard-coded rule format
			ruleMap.put(JERunnerRuleMapping.FORMAT, "DRL");
			
			JEResponse jeRunnerResp = null;
			if(ruleIsBuilt)
			{
				 jeRunnerResp = JERunnerAPIHandler.addRule(ruleMap);

			}
			else
			{
				 jeRunnerResp = JERunnerAPIHandler.updateRule(ruleMap);
				 userDefinedRule.setBuilt(true);

			}
			if (jeRunnerResp == null || jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
				throw new RuleBuildFailedException(jeRunnerResp.getMessage());
			}

		}
	}
}
