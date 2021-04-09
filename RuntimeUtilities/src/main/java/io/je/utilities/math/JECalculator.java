package io.je.utilities.math;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class JECalculator {

	
	
	
	/*
	 * exp
	 */
	public static long exp(double x) {
		return (long) Math.exp(x);

	}
	
	/*
	 * exp
	 */
	public static long exp(int x) {
		return (long) Math.exp(x);

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
	public static long factorial(int x) {
		return CombinatoricsUtils.factorial(x);

	}

	/*
	 * square
	 */
	public static long square(Double x) {
		return CombinatoricsUtils.factorial(x.intValue());

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
