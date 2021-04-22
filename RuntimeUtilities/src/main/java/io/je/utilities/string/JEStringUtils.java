package io.je.utilities.string;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class JEStringUtils {

    public static String substring(String base, int start, int end) {
        return StringUtils.substring(base, start, end);
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }
    
    public static String capitalize(String str)
    {
    	return StringUtils.capitalize(str);
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return (str != null) &&((!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }



}
