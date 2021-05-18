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


	public static String getRuleIdFromErrorMsg(String message) {
		// TODO Auto-generated method stub
		return  StringUtils.substringBetween(message, "\"[", "]");

	}
	
	public static void main(String[] args)
	{
		String test = "Error evaluating constraint 'location > 10' in [Rule \"[f607ac1f-923d-e24b-199f-e0d8f4da9a5e]azerty1\" in D:\\IOJobEngine\\SumTest\\target\\classes\\com\\sample\\rules\\Sample.drl]";
		System.out.println(StringUtils.substringBefore(test, " in "));
	}


}
