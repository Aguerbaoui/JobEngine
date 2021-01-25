package io.je.rulebuilder.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.ScriptedRule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.apis.JERunnerRuleMapping;
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
	public static void buildRule(JERule jeRule, String buildPath)
			throws RuleBuildFailedException, JERunnerUnreachableException, IOException {
		String rulePath = "";
		boolean ruleIsAdded = jeRule.isBuilt();
		if( jeRule instanceof UserDefinedRule) {
			List<ScriptedRule> unitRules = ((UserDefinedRule) jeRule).scriptRule();
			for (ScriptedRule rule : unitRules) {
				// generate drl
				 rulePath = rule.generateDRL(buildPath);
				sendDRLToJeRunner(jeRule,rulePath,ruleIsAdded);
				}
		}
		if( jeRule instanceof ScriptedRule)
		{
			 rulePath = ((ScriptedRule) jeRule).generateDRL(buildPath);
			sendDRLToJeRunner(jeRule,rulePath,ruleIsAdded);
		}
		
		 jeRule.setAdded(true);
		 jeRule.setBuilt(true);
	}
		
	
	/*
	 * send rule to JERunner
	 */
	public static void sendDRLToJeRunner(JERule rule, String path, boolean ruleIsAdded) throws JERunnerUnreachableException, IOException, RuleBuildFailedException
	{
		

			// compile rule

			HashMap<String, String> ruleMap = new HashMap<>();
			ruleMap.put(JERunnerRuleMapping.PROJECT_ID, rule.getJobEngineProjectID());
			ruleMap.put(JERunnerRuleMapping.PATH, path);
			ruleMap.put(JERunnerRuleMapping.RULE_ID, rule.getJobEngineElementID());

			// TODO: remove hard-coded rule format
			ruleMap.put(JERunnerRuleMapping.FORMAT, "DRL");
			ruleMap.put(JERunnerRuleMapping.TOPICS, rule.getTopics().toString());

			
			JEResponse jeRunnerResp = null;
			if(ruleIsAdded)
			{
				 jeRunnerResp = JERunnerAPIHandler.addRule(ruleMap);

			}
			else
			{
				 jeRunnerResp = JERunnerAPIHandler.updateRule(ruleMap);
				

			}
			if (jeRunnerResp == null || jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
				throw new RuleBuildFailedException(jeRunnerResp.getMessage());
			}

	}

}