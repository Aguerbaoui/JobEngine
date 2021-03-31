package io.je.utilities.ruleutils;

import org.apache.commons.lang3.StringUtils;

public class RuleIdManager {
	
	
	public static String generateSubRulePrefix(String ruleId)
	{
		return "_" + ruleId + "_";
	}
	
	
	public static String retrieveIdFromSubRuleName(String subRuleName)
	{
		return StringUtils.substringBetween(subRuleName, "_", "_");
	}

}
