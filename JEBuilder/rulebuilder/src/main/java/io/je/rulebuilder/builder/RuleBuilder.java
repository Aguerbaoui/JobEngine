package io.je.rulebuilder.builder;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.config.JERunnerRuleMapping;
import io.je.utilities.constants.APIConstants;
import io.je.utilities.constants.ClassBuilderErrors;
import io.je.utilities.constants.JEGlobalconfig;
import io.je.utilities.exceptions.JERunnerUnreachableException;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.network.Network;

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

			Response response = null;
			try {
				response = Network.makeNetworkCallWithJsonBodyWithResponse(ruleMap,
						JEGlobalconfig.RUNTIME_MANAGER_BASE_API + APIConstants.COMPILERULE);

			} catch (ConnectException e) {
				throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
			}

			if (response == null || response.code() == 404) {
				throw new JERunnerUnreachableException(ClassBuilderErrors.jeRunnerUnreachable);
			}

			String respBody = response.body().string();
			ObjectMapper objectMapper = new ObjectMapper();
			io.je.utilities.network.JEResponse jeRunnerResp = objectMapper.readValue(respBody,
					io.je.utilities.network.JEResponse.class);
			if (jeRunnerResp.getCode() != 0) {
				throw new RuleBuildFailedException(jeRunnerResp.getMessage());
			}

		}
	}
}
