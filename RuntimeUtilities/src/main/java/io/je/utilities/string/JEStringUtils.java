package io.je.utilities.string;

import org.apache.commons.lang3.StringUtils;

public class JEStringUtils {

    public static String substring(String base, int start, int end) {
        return StringUtils.substring(base, start, end);
    }

    public static boolean isEmpty(String str) {
        // TODO Auto-generated method stub
        return StringUtils.isEmpty(str);
    }
}
