package io.je.runtime.repos;

import io.je.utilities.beans.JEVariable;
import io.je.utilities.exceptions.ClassLoadException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class VariableManager {

    public static ConcurrentHashMap<String, HashMap<String, JEVariable>> variables = new ConcurrentHashMap<>();

    public static void addVariable(JEVariable variable) {
        if(!variables.containsKey(variable.getJobEngineProjectID())) {
            variables.put(variable.getJobEngineProjectID(), new HashMap<>());
        }
        variables.get(variable.getJobEngineProjectID()).put(variable.getJobEngineElementID(),variable);
    }

    public static JEVariable getJeVariable(String projectId, String id) {
        return variables.get(projectId).get(id);
    }

    public static void removeVariable(String projectId, String id) {
        if(variables.containsKey(projectId)) {
            variables.get(projectId).remove(id);
        }
    }

    /*
     * returns the class type based on a string defining the type
     */
    public static Class<?> getType(String type) {
        type = type.toUpperCase();
        Class<?> classType = null;
        switch (type) {
            case "BYTE":
                classType = byte.class;
                break;
            case "SBYTE":
                classType = byte.class;
                break;
            case "INT":
                classType = int.class;
                break;
            case "SHORT":
                classType = short.class;
                break;
            case "LONG":
                classType = long.class;
                break;
            case "FLOAT":
                classType = float.class;
                break;
            case "DOUBLE":
                classType = double.class;
                break;
            case "CHAR":
                classType = char.class;
                break;
            case "BOOL":
                classType = boolean.class;
                break;
            case "OBJECT":
                classType = Object.class;
                break;
            case "STRING":
                classType = String.class;
                break;
            case "DATETIME":
                classType = LocalDateTime.class;
                break;
            case "VOID":
                classType = void.class;
                break;
            default:
                break; //add default value
        }
        return classType;
    }

}
