package io.je.rulebuilder.builder;

import java.util.List;

import io.je.rulebuilder.components.JERule;
import io.je.rulebuilder.components.UserDefinedRule;
import io.je.rulebuilder.config.LogConstants;
import io.je.rulebuilder.models.RuleModel;
import io.je.utilities.exceptions.RuleBuildFailedException;
import io.je.utilities.exceptions.RuleNotAddedException;


/*
 * Rule Builder class that builds .drl file from JERule instance
 */
public class RuleBuilder {
	
	
	/*
	 * TODO: function to be moved to rule service
	 * creates a new rule from the Rule Model
	 */
	public static UserDefinedRule createRule(RuleModel rule) throws RuleNotAddedException 
	{
	
		return new UserDefinedRule(rule);
	}
	
	

	/*
	 * generate drl file from rules and saves them to the provided path 
	 */
	public static void buildRule(UserDefinedRule userDefinedRule,String buildPath) throws RuleBuildFailedException
	{
		List<JERule> unitRules = userDefinedRule.build();
		for(JERule rule : unitRules)
		{
			//rule.generateDrl(); 
		}
	}
}
