package io.je.utilities.math;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class JECalculator {
	
	
	/*
	 * factorial
	 */
	public static long factorial(Double x)
	{
	    return CombinatoricsUtils.factorial(x.intValue());

	}
	
	

	/*
	 * square
	 */
	public static long square(Double x)
	{
	    return CombinatoricsUtils.factorial(x.intValue());

	}
	
	/*
	 * change sign
	 */
	public static long changeSign(Double x)
	{
	    return (long) -x;

	}
	
	
	/*
	 * change sign
	 */
	public static long multiplicativeInverse(Double x)
	{
	    return (long)(1/x);

	}
	
	

	

}
