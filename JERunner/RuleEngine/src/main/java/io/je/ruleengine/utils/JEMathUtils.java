package io.je.ruleengine.utils;

import io.je.utilities.log.JELogger;
import utils.log.LogSubModule;
import utils.maths.MathUtilities;

public class JEMathUtils {
	
	
	
	public static double castToDouble(Object x)
	{
		if(x instanceof Float)
		{
			return ((Float)x).doubleValue();
		}
		return (double)x;
	}
	
	
	
	public static boolean DivisionByZero(String projectId, String ruleId, String blockId,double a)
	{
		if(a==0)
    	{
    		JELogger.error("Division by 0 is not allowed", null, projectId, LogSubModule.RULE, ruleId,blockId);
    		return false;
    	}
		return true;
	}
	

	

}
