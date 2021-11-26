package io.je.utilities.ruleutils;

import org.apache.commons.lang3.StringUtils;

public class IdManager {
	
	
	public static String generateSubRulePrefix(String ruleId)
	{
		return "_" + ruleId + "_";
	}
	
	
	public static String retrieveIdFromSubRuleName(String subRuleName)
	{
		return StringUtils.substringBetween(subRuleName, "_", "_");
	}

	public static String retrieveNameFromSubRuleName(String subRuleName)
	{
		return StringUtils.substringAfterLast(subRuleName, "_");
	}
	

	public static String getRuleIdFromErrorMsg(String message) {
		// TODO Auto-generated method stub
		return  StringUtils.substringBetween(message, "\"[", "]");

	}

	public static String getScriptTaskId(String wfName, String blockName) {
		return wfName + blockName;
	}
	


}
