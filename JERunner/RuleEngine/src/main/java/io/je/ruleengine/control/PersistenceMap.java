package io.je.ruleengine.control;

import java.util.HashMap;

public class PersistenceMap {

    // String : rule ID
    static HashMap<String, Persistence> rulesPersistenceMap = new HashMap<>();

    public static HashMap<String, Persistence> getRulesPersistenceMap() {
        return rulesPersistenceMap;
    }

    public static void setRulesPersistenceMap(HashMap<String, Persistence> rulesPersistenceMap) {
        PersistenceMap.rulesPersistenceMap = rulesPersistenceMap;
    }

}
