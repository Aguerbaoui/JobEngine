package io.je.ruleengine.utils;

import io.je.utilities.exceptions.CastToDoubleException;
import io.je.utilities.log.JELogger;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

/**
 * Validation of inputs used in DRLs
 **/
public class JEMathUtils {


    public static double castToDouble(String projectId, String ruleId, String blockId, Object x) throws CastToDoubleException {
        try {
            if (x instanceof Float) {
                return ((Float) x).doubleValue();
            } else if (x instanceof Integer) {
                return ((Integer) x).doubleValue();
            } else if (x instanceof Long) {
                return ((Long) x).doubleValue();
            } else if (x instanceof Short) {
                return ((Short) x).doubleValue();
            } else if (x instanceof Byte) {
                return ((Byte) x).doubleValue();
            }

            return (double) x;
        } catch (Exception exp) {
            JELogger.error(exp.getMessage(), LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockId);
            LoggerUtils.logException(exp);
            throw new CastToDoubleException(exp.getMessage());
        }
    }

    public static boolean divisionByZero(String projectId, String ruleId, String blockId, double a) {
        if (a == 0) {
            JELogger.error(blockId + ": Division by 0 is not allowed", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockId);
            return false;
        }
        return true;
    }

    public static boolean factorialConstraint(String projectId, String ruleId, String blockId, double a) {
        if (a <= 0 || a > 20) {
            JELogger.error(blockId + ": Input must be between 0 and 20.", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockId);
            return false;
        }
        return true;
    }

    public static boolean strictlyPositive(String projectId, String ruleId, String blockId, double a) {
        if (a <= 0) {
            JELogger.error(blockId + ": Input has to be strictly positive.", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockId);
            return false;
        }
        return true;
    }

    public static boolean positive(String projectId, String ruleId, String blockId, double a) {
        if (a < 0) {
            JELogger.error(blockId + ": Input has to be  positive.", LogCategory.RUNTIME, projectId, LogSubModule.RULE, ruleId, blockId);
            return false;
        }
        return true;
    }

    public String asDouble(String val) {
        return "JEMathUtils.castToDouble(" + val + " )"; //" Double.valueOf( "+val+" )";
    }
}

