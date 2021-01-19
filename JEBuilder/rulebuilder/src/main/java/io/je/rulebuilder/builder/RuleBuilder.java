package io.je.rulebuilder.builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.je.rulebuilder.components.DrlRule;
import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.ScriptedRule;
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
	public static void buildRule(JERule jeRule, String buildPath)
			throws RuleBuildFailedException, JERunnerUnreachableException, IOException {
		String rulePath = "";
		boolean ruleIsBuilt = jeRule.isBuilt();
		if( jeRule instanceof UserDefinedRule) {
			List<DrlRule> unitRules = ((UserDefinedRule) jeRule).build();
			for (DrlRule rule : unitRules) {
				// generate drl
				 rulePath = rule.generateDRL(buildPath);
				sendDRLToJeRunner(rulePath,buildPath,ruleIsBuilt);
				}
		}
		if( jeRule instanceof ScriptedRule)
		{
			 rulePath = ((ScriptedRule) jeRule).generateDRL(buildPath);
				sendDRLToJeRunner(rulePath,jeRule.getJobEngineProjectID(),ruleIsBuilt);
		}
		
		 jeRule.setBuilt(true);
	}
		
	
	public static void sendDRLToJeRunner(String rulePath,String ruleId,boolean ruleIsBuilt) throws JERunnerUnreachableException, IOException, RuleBuildFailedException
	{
		

			// compile rule

			HashMap<String, String> ruleMap = new HashMap<>();
			ruleMap.put(JERunnerRuleMapping.PROJECT_ID, ruleId);
			ruleMap.put(JERunnerRuleMapping.PATH, rulePath);
			ruleMap.put(JERunnerRuleMapping.RULE_ID, ruleId);

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
				

			}
			if (jeRunnerResp == null || jeRunnerResp.getCode() != ResponseCodes.CODE_OK) {
				throw new RuleBuildFailedException(jeRunnerResp.getMessage());
			}

	}

}