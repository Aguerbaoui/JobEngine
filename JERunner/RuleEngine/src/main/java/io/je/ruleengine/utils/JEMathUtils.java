package io.je.ruleengine.utils;

import io.je.utilities.log.JELogger;
import utils.log.LogSubModule;

public class JEMathUtils {
	
	
	
	public static double castToDouble(Object x)
	{
		if(x instanceof Float)
		{
			return ((Float)x).doubleValue();
		}
		return (double)x;
	}
	
	
	
	public static boolean divisionByZero(String projectId, String ruleId, String blockId,double a)
	{
		if(a==0)
    	{
    		JELogger.error(blockId+": Division by 0 is not allowed", null, projectId, LogSubModule.RULE, ruleId,blockId);
    		return false;
    	}
		return true;
	}
	
	public static boolean factorialConstraint(String projectId, String ruleId, String blockId,double a)
	{
		if(a<=0|| a >20)
    	{
			JELogger.error(blockId+": Input must be between 0 and 20.", null, projectId, LogSubModule.RULE, ruleId,blockId);
    		return false;
    	}
		return true;
	}
	
	
	public static boolean strictlyPositive(String projectId, String ruleId, String blockId,double a)
	{
		if(a<=0)
    	{
			JELogger.error(blockId+": Input has to be strictly positive.", null, projectId, LogSubModule.RULE, ruleId,blockId);
    		return false;
    	}
		return true;
	}
	
	public static boolean positive(String projectId, String ruleId, String blockId,double a)
	{
		if(a<0)
    	{
			JELogger.error(blockId+": Input has to be  positive.", null, projectId, LogSubModule.RULE, ruleId,blockId);
    		return false;
    	}
		return true;
	}

	

}
