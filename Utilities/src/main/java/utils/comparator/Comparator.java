package utils.comparator;

import utils.log.LoggerUtils;

public class Comparator {

    public static boolean isSameValue(Object a, Object b) {
        boolean result = false;
        try {
            if (a == b || a.equals(b)) {
                return true;
            }

            String aStr = String.valueOf(a);
            String bStr = String.valueOf(b);

            if (aStr.equals(bStr) || Double.valueOf(aStr).equals(Double.valueOf(bStr))) {
                return true;
            }

        } catch (Exception e) {
            LoggerUtils.logException(e);
        }

        return result;

    }

}
