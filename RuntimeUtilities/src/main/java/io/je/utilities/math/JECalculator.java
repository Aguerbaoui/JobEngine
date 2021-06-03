package io.je.utilities.math;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class JECalculator {

	/*
	 * ln
	 */
	public static double ln(Object x)
	{
		return  Math.log((double) x);
	}
	/*
	 * ceil
	 */
	public static double ceil(Object x)
	{
		return  Math.ceil((double) x);
	}
	
	
	
	/*
	 * truncate
	 */
	public static double truncate(Object x)
	{
		return  Math.round((double) x);
	}
	
	/*
	 * floor
	 */
	public static double floor(Object x)
	{
		return  Math.floor((double) x);
	}
	
	/*
	 * sin
	 */
	public static double sin(Object x)
	{
		return  Math.sin((double) x);
	} 
	
	/*
	 * cos
	 */
	public static double cos(Object x)
	{
		return  Math.cos((double) x);
	} 
	
	/*
	 * asin
	 */
	public static double asin(Object x)
	{
		return  Math.asin((double) x);
	}
	
	
	/*
	 * arctan
	 */
	public static double atan(Object x)
	{
		return  Math.atan((double) x);
	}
	

	/*
	 * tan
	 */
	public static double tan(Object x)
	{
		return  Math.tan((double) x);
	}
	
	/*
	 * square
	 */
	public static double square(Object x)
	{
		return  Math.pow((double) x,2);
	}
	/*
	 * sqrt
	 */
	public static double sqrt(Object x)
	{
		return  Math.sqrt((double) x);
	}
	/*
	 * acos
	 */
	public static double acos(Object x)
	{
		return  Math.acos((double) x);
	}/*
	 * abs
	 */
	public static double abs(Object x)
	{
		return  Math.abs((double) x);
	}
	/*
	 * log10
	 */
	public static double log10(Object x)
	{
		return  Math.log10((double) x);
	}
	
	
	/*
	 * power
	 */
	public static double power(Object x, int i)
	{
		return  Math.pow((double) x, i);
	}
	

	/*
	 * exp
	 */
	public static long exp(Object x) {
		return (long) Math.exp((double) x);

	}
	

	
	/*
	 * factorial
	 */
	public static long factorial(double x) {
		Double value = x;
		return CombinatoricsUtils.factorial(value.intValue());

	}
	
	/*
	 * factorial
	 */
	public static long factorial(Object x) {
		return CombinatoricsUtils.factorial((int) x);

	}

	/*
	 * change sign
	 */
	public static long changeSign(Object x) {
		return  -(long)x;

	}
	

	/*
	 * change sign
	 */
	public static long changeSign(Double x) {
		return (long) -x;

	}

	/*
	 * multiplicative Inverse
	 */
	public static long multiplicativeInverse(Double x) {
		return (long) (1 / x);

	}
	
	
	
	/*
	 * multiplicative Inverse
	 */
	public static long multiplicativeInverse(long x) {
		return (long) (1 / x);

	}

	/*
	 * multiplicative Inverse
	 */
	public static long multiplicativeInverse(int x) {
		return (long) (1 / x);

	}
	/*
	 * multiplicative Inverse
	 */
	public static long multiplicativeInverse(float x) {
		return (long) (1 / x);

	}
	
	/*
	 * highLimiting returns min ( limitValue, max(Inputs..) )
	 */
	public static long highLimiting(double limit, double... numbers) {
		double max = maximum(numbers);

		return (long) Math.min(limit, max);

	}

	/*
	 * low Limiting returns min ( limitValue, min(Inputs..) )
	 */
	public static long lowLimiting(double limit, double... numbers) {
		double min = minimum(numbers);

		return (long) Math.min(limit, min);

	}

	public static long minimum(double... numbers) {
		double min = numbers[0];
		for (double number : numbers) {
			if (number < min) {
				min = number;
			}
		}

		return (long) min;

	}
	
	/*
	 * sum
	 */
	public static long sum(double... numbers) {
		double sum = 0;
		for (double number : numbers) {
			sum+=number;
		}
		return (long) sum;
	}
	
	
	/*
	 * multiply
	 */
	public static long multiply(double... numbers) {
		double result = 1;
		for (double number : numbers) {
			result*=number;
		}
		return (long) result;
	}
	
	/*
	 * subtract
	 */
	public static long subtract(double a , double b) {
		
		return (long) (a-b);
	}
	
	
	
	/*
	 * divide
	 */
	public static long divide(double a , double b) {
		
		return (long) (a/b);
	}
	

	public static long maximum(double... numbers) {
		double max = numbers[0];
		for (double number : numbers) {
			if (number > max) {
				max = number;
			}
		}

		return (long) max;

	}

	
	

}
