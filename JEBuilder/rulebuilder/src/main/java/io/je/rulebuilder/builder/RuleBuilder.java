package io.je.rulebuilder.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.ScriptedRule;
import io.je.utilities.apis.JERunnerAPIHandler;
import io.je.utilities.constants.ResponseCodes;
import io.je.utilities.exceptions.JERunnerErrorException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.logger.JELogger;
import io.je.utilities.mapping.JERunnerRuleMapping;
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
			throws RuleBuildFailedException, JERunnerErrorException, IOException {
		String rulePath = "";
		boolean ruleIsAdded = jeRule.isAdded();
		JELogger.trace(RuleBuilder.class, "Building rule with id = " + jeRule.getJobEngineElementID());
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
		
	}
		
	
	/*
	 * send rule to JERunner
	 */
	public static void sendDRLToJeRunner(JERule rule, String path, boolean ruleIsAdded) throws JERunnerErrorException, RuleBuildFailedException, IOException
	{
		

			// compile rule

			HashMap<String, Object> ruleMap = new HashMap<>();
			ruleMap.put(JERunnerRuleMapping.PROJECT_ID, rule.getJobEngineProjectID());
			ruleMap.put(JERunnerRuleMapping.PATH, path);
			ruleMap.put(JERunnerRuleMapping.RULE_ID, rule.getJobEngineElementID());

			// TODO: remove hard-coded rule format
			ruleMap.put(JERunnerRuleMapping.FORMAT, "DRL");
			ruleMap.put(JERunnerRuleMapping.TOPICS, rule.getTopics());

			JELogger.trace(RuleBuilder.class, "Sending rule build request to runner, project id = " + rule.getJobEngineProjectID() + "rule id = " + rule.getJobEngineElementID());
			
			JEResponse jeRunnerResp = null;
			if(!ruleIsAdded)
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