package io.je.runtime.repos;

import io.je.utilities.beans.JEVariable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class VariableManager {

    //Project id => VarId => JEVariable
    public static ConcurrentHashMap<String, HashMap<String, JEVariable>> variables = new ConcurrentHashMap<>();

    /*
    * Add variable to variable manager
    * */
    public static void addVariable(JEVariable variable) {
        if(!variables.containsKey(variable.getJobEngineProjectID())) {
            variables.put(variable.getJobEngineProjectID(), new HashMap<>());
        }
        variables.get(variable.getJobEngineProjectID()).put(variable.getJobEngineElementID(),variable);
    }

    /*
     * Returns the equialent jevariable
     * */
    public static JEVariable getJeVariable(String projectId, String id) {
        return variables.get(projectId).get(id);
    }

    /*
     * Remove variable from variable manager
     * */
    public static void removeVariable(String projectId, String id) {
        if(variables.containsKey(projectId)) {
            variables.get(projectId).remove(id);
        }
    }


}
