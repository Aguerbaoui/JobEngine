package utils.string;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringSub {

    //Project variable substitutions
    private static ConcurrentHashMap<String, HashMap<String, Object>> variables = new ConcurrentHashMap<>();

    private static org.apache.commons.text.StringSubstitutor substitutor = null;

    public static org.apache.commons.text.StringSubstitutor getStringSubstitutor(HashMap<String, Object> variables) {
        if (substitutor == null) substitutor = new org.apache.commons.text.StringSubstitutor();
        return substitutor;
    }

    public static void addVariable(String projectId, String varName, Object value) {
        if (!variables.containsKey(projectId)) {
            variables.put(projectId, new HashMap<>());
        }
        variables.get(projectId).put(varName, value);
    }

    public static String replace(String projectId, String toBeReplaced) {
        if (variables.containsKey(projectId)) {
            //HashMap<String, Object> subMap = variables.get(projectId);
            return substitutor.replace(toBeReplaced, variables.get(projectId));
        }
        else return toBeReplaced;
    }

    public static String replace(String toBeReplaced, HashMap<String, Object> vars) {
        return substitutor.replace(toBeReplaced, vars);
    }

    public static void main(String[] args) {
        try {
            //String templateString = "The account ${accountNumber} balance is ${balance} dollars.";

            String templateString = "{\"projectId\":\"${testVarName}\",\n" +
                    "\"projectName\":\"123\",\n" +
                    " \"configurationPath\":\"D:\\\\test\"\n" +
                    "}";


            String test =  "{\n" +
                    "  \"id\":\"${testVar}\"\n" +
                    "}";


            // the value map does not define "balance" variable
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("testVar", "123");

            org.apache.commons.text.StringSubstitutor stringSubstitutor = new org.apache.commons.text.StringSubstitutor();

            // Sets this flag to true to throw exception if any variable is undefined.
            stringSubstitutor.setEnableUndefinedVariableException(true);
            String result = stringSubstitutor.replace(test, valuesMap);

            System.out.println(result);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}