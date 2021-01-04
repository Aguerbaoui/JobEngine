package io.je.rulebuilder.builder;

import java.util.List;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.models.RuleModel;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;


/*
 * Rule Builder class that builds .drl file from JERule instance
 */
public class RuleBuilder {
	


	/*
	 * generate drl file from rules and saves them to the provided path 
	 */
	public static void buildRule(UserDefinedRule userDefinedRule,String buildPath) throws RuleBuildFailedException
	{
		List<JERule> unitRules = userDefinedRule.build();
		for(JERule rule : unitRules)
		{
			//generateDRL
		
			rule.generateDRL(buildPath); 
			
			//CompileAllRules()
		}
	}
}
