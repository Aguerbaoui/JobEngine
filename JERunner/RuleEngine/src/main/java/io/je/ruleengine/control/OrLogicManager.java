package io.je.ruleengine.control;

import java.util.HashMap;

public class OrLogicManager {

    // String : ruleId, Integer : condition matches count
    private static HashMap<String, Integer> matchCounters = new HashMap<>();


    public static HashMap<String, Integer> getMatchCounters() {
        return matchCounters;
    }

    public static void setMatchCounters(HashMap<String, Integer> matchCounters) {
        OrLogicManager.matchCounters = matchCounters;
    }

    public static Integer getRuleMatchCounter(String ruleName) {
        return OrLogicManager.matchCounters.get(ruleName);
    }

    public static void addRuleMatch(String ruleName) {
        Integer matchCounter = OrLogicManager.matchCounters.get(ruleName);

        if (matchCounter != null) {
            OrLogicManager.matchCounters.put(ruleName, ++matchCounter);
        } else {
            OrLogicManager.matchCounters.put(ruleName, Integer.valueOf(1));
        }
    }

    public static void resetRuleMatch(String ruleName) {
        OrLogicManager.matchCounters.put(ruleName, Integer.valueOf(0));
    }

}
