package utils.maths;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class MathUtilities {

    public static int decimalPrecision = 3;

    /*
     * ln
     */
    public static double ln(Object x) {

        return Math.log(castToDouble(x));
    }

    public static double castToDouble(Object x) {
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
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*
     * ceil
     */
    public static double ceil(Object x) {

        return Math.ceil(castToDouble(x));
    }


    /*
     * truncate
     */
    public static double truncate(Object x) {

        return Math.round(castToDouble(x));
    }

    /*
     * floor
     */
    public static double floor(Object x) {

        return Math.floor(castToDouble(x));
    }

    /*
     * sin
     */
    public static double sin(Object x) {

        return Math.sin(castToDouble(x));
    }

    /*
     * cos
     */
    public static double cos(Object x) {

        return Math.cos(castToDouble(x));
    }

    /*
     * asin
     */
    public static double asin(Object x) {

        return Math.asin(castToDouble(x));
    }


    /*
     * arctan
     */
    public static double atan(Object x) {

        return Math.atan(castToDouble(x));
    }


    /*
     * tan
     */
    public static double tan(Object x) {

        return Math.tan(castToDouble(x));
    }

    /*
     * square
     */
    public static double square(Object x) {

        return Math.pow(castToDouble(x), 2);
    }

    /*
     * sqrt
     */
    public static double sqrt(Object x) {

        return Math.sqrt(castToDouble(x));
    }

    /*
     * acos
     */
    public static double acos(Object x) {

        return Math.acos(castToDouble(x));
    }/*
     * abs
     */

    public static double abs(Object x) {
        return Math.abs(castToDouble(x));
    }


    /*
     * log10
     */
    public static double log10(Object x) {

        return Math.log10(castToDouble(x));
    }


    /*
     * power
     */
    public static double power(Object x, int i) {

        return Math.pow(castToDouble(x), i);
    }


    /**
     * bias
     */
    public static double bias(Object x, int i) {

        return castToDouble(x) + i;
    }

    /**
     * gain
     */
    public static double gain(Object x, int i) {

        return castToDouble(x) * i;
    }

    /*
     * exp
     */
    public static double exp(Object x) {

        return Math.exp(castToDouble(x));

    }


    /*
     * factorial
     */
    public static double factorial(Object x) {
        Double value = castToDouble(x);
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

    /*
     * Low Limiting returns max ( limitValue, min(Inputs..) )
     */
    public static double lowLimiting(double limit, double... numbers) {
        double min = minimum(numbers);

        return (double) Math.max(limit, min);

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
