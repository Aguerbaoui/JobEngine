package io.je.utilities.ruleutils;

import org.apache.commons.lang3.StringUtils;

public class IdManager {


    public static String generateSubRulePrefix(String ruleId) {
        return "_" + ruleId + "_";
    }


    public static String retrieveIdFromSubRuleName(String subRuleName) {
        return StringUtils.substringBetween(subRuleName, "_", "_");
    }

    public static String retrieveNameFromSubRuleName(String subRuleName) {
        return StringUtils.substringAfterLast(subRuleName, "_");
    }


    public static String getRuleIdFromErrorMsg(String message) {
        // TODO Auto-generated method stub
        //Exp: Error evaluating constraint '(double) Getter35temp > JEMathUtils.castToDouble(0 )' in [Rule "_295f12b8-e75a-3c78-3284-e2f962b4ea78_rr1" in _295f12b8-e75a-3c78-3284-e2f962b4ea78_rr1.drl]
        String ruleId;
        ruleId = StringUtils.substringBetween(message, "\"[", "]");
        if (ruleId == null) {
            ruleId = StringUtils.substringBetween(message, "[Rule \"_", "_");
        }
        return ruleId;

    }

    public static String getScriptTaskId(String wfName, String blockName) {
        return wfName + blockName;
    }


}
