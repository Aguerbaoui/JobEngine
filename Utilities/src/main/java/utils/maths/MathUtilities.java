package utils.maths;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class MathUtilities {

    public static int decimalPrecision = 3;

    /*
     * ln
     */
    public static double ln(double x) {

        return Math.log(x);
    }


    /*
     * ceil
     */
    public static double ceil(double x) {

        return Math.ceil(x);
    }


    /*
     * truncate
     */
    public static double truncate(double x) {

        return Math.round(x);
    }

    /*
     * floor
     */
    public static double floor(double x) {

        return Math.floor(x);
    }

    /*
     * sin
     */
    public static double sin(double x) {

        return Math.sin(x);
    }

    /*
     * cos
     */
    public static double cos(double x) {

        return Math.cos(x);
    }

    /*
     * asin
     */
    public static double asin(double x) {

        return Math.asin(x);
    }


    /*
     * arctan
     */
    public static double atan(double x) {

        return Math.atan(x);
    }


    /*
     * tan
     */
    public static double tan(double x) {

        return Math.tan(x);
    }

    /*
     * square
     */
    public static double square(double x) {

        return Math.pow(x, 2);
    }

    /*
     * sqrt
     */
    public static double sqrt(double x) {

        return Math.sqrt(x);
    }

    /*
     * acos
     */
    public static double acos(double x) {

        return Math.acos(x);
    }/*
     * abs
     */

    public static double abs(double x) {
        return Math.abs(x);
    }


    /*
     * log10
     */
    public static double log10(double x) {

        return Math.log10(x);
    }


    /*
     * power
     */
    public static double power(double x, int i) {

        return Math.pow(x, i);
    }


    /**
     * bias
     */
    public static double bias(double x, int i) {

        return x + i;
    }

    /**
     * gain
     */
    public static double gain(double x, int i) {

        return x * i;
    }

    /*
     * exp
     */
    public static double exp(double x) {

        return Math.exp(x);

    }


    /*
     * factorial
     */
    public static double factorial(double x) {
        Double value = x;
        if (value.intValue() > 0 && value <= 20) {
            return CombinatoricsUtils.factorial(value.intValue());

        } else {
            return 1;

        }
    }


    /*
     * change sign
     */
    public static double changeSign(Object x) {
        return -(double) x;

    }

    /*
     * change sign
     */
    public static double changeSign(float x) {
        return -(double) x;

    }


    /*
     * change sign
     */
    public static double changeSign(Double x) {
        return (double) -x;

    }

    /*
     * multiplicative Inverse
     */
    public static double multiplicativeInverse(Double x) {
        return (double) (1 / x);

    }


    /*
     * multiplicative Inverse
     */
    public static double multiplicativeInverse(double x) {
        return (double) (1 / x);

    }

    /*
     * multiplicative Inverse
     */
    public static double multiplicativeInverse(int x) {
        return (double) (1 / x);

    }

    /*
     * multiplicative Inverse
     */
    public static double multiplicativeInverse(float x) {
        return (double) (1 / x);

    }

    /*
     * High Limiting returns min ( limitValue, max(Inputs..) )
     */
    public static double highLimiting(double limit, double... numbers) {
        double max = maximum(numbers);

        return (double) Math.min(limit, max);

    }

    public static double maximum(double... numbers) {
        double max = numbers[0];
        for (double number : numbers) {
            if (number > max) {
                max = number;
            }
        }

        return (double) max;

    }

    /*
     * Low Limiting returns max ( limitValue, min(Inputs..) )
     */
    public static double lowLimiting(double limit, double... numbers) {
        double min = minimum(numbers);

        return (double) Math.max(limit, min);

    }

    public static double minimum(double... numbers) {
        double min = numbers[0];
        for (double number : numbers) {
            if (number < min) {
                min = number;
            }
        }

        return (double) min;

    }

    /*
     * sum
     */
    public static double sum(double... numbers) {
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return (double) sum;
    }

    /*
     * average
     */
    public static double average(double... numbers) {
        double sum = 0;
        int count = 0;
        for (double number : numbers) {
            sum += number;
            count++;
        }
        if (count <= 0) {
            return 0;
        }
        return (double) sum / count;
    }

    /*
     * multiply
     */
    public static double multiply(double... numbers) {
        double result = 1;
        for (double number : numbers) {
            result *= number;
        }
        return (double) result;
    }

    /*
     * subtract
     */
    public static double subtract(double a, double b) {

        return (double) (a - b);
    }

    /*
     * divide
     */
    public static double divide(double a, double b) {

        return a / b;
    }


}
