package io.je.ruleengine.utils;

import io.je.utilities.log.JELogger;
import utils.log.LogSubModule;

public class JEMathUtils {
	
    /*
     * divide
     */
    public static double divide(String projectId, String ruleId, String blockId, double a , double b) {

    	if(b==0)
    	{
    		JELogger.error("Division by 0 is not allowed", null, projectId, LogSubModule.RULE, ruleId,blockId);
    	}
    	
        return  a/b;
    }
	

}
