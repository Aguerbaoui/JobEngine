package utils.maths;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class MathUtilities {


    /*
     * ln
     */
    public static double ln(Object x)
    {
        Object value ;
        try{
            value = Math.log((double) x);
        }catch(Exception e)
        {
            value= Math.log((long) x);
        }
        return (double) value;
    }
    /*
     * ceil
     */
    public static double ceil(Object x)
    {
        Object value ;
        try{
            value = Math.ceil((double) x);
        }catch(Exception e)
        {
            value= Math.ceil((long) x);
        }
        return (double) value;
    }





    /*
     * truncate
     */
    public static double truncate(Object x)
    {
        Object value ;
        try{
            value = Math.round((double) x);
        }catch(Exception e)
        {
            value= Math.round((long) x);
        }
        return (double) value;
    }

    /*
     * floor
     */
    public static double floor(Object x)
    {
        Object value ;
        try{
            value = Math.floor((double) x);
        }catch(Exception e)
        {
            value= Math.floor((long) x);
        }
        return (double) value;	}

    /*
     * sin
     */
    public static double sin(Object x)
    {
        Object value ;
        try{
            value = Math.sin((double) x);
        }catch(Exception e)
        {
            value= Math.sin((long) x);
        }
        return (double) value;
    }

    /*
     * cos
     */
    public static double cos(Object x)
    {
        Object value ;
        try{
            value = Math.cos((double) x);
        }catch(Exception e)
        {
            value= Math.cos((long) x);
        }
        return (double) value;
    }

    /*
     * asin
     */
    public static double asin(Object x)
    {
        Object value ;
        try{
            value = Math.asin((double) x);
        }catch(Exception e)
        {
            value= Math.asin((long) x);
        }
        return (double) value;	}


    /*
     * arctan
     */
    public static double atan(Object x)
    {
        Object value ;
        try{
            value = Math.atan((double) x);
        }catch(Exception e)
        {
            value= Math.atan((long) x);
        }
        return (double) value;
    }


    /*
     * tan
     */
    public static double tan(Object x)
    {
        Object value ;
        try{
            value = Math.tan((double) x);
        }catch(Exception e)
        {
            value= Math.tan((long) x);
        }
        return (double) value;
    }

    /*
     * square
     */
    public static double square(Object x)
    {
        Object value ;
        try{
            value = Math.pow((double) x,2);
        }catch(Exception e)
        {
            value= Math.pow((long) x,2);
        }
        return (double) value;
    }
    /*
     * sqrt
     */
    public static double sqrt(Object x)
    {
        Object value ;
        try{
            value = Math.sqrt((double) x);
        }catch(Exception e)
        {
            value= Math.sqrt((long) x);
        }
        return (double) value;
    }
    /*
     * acos
     */
    public static double acos(Object x)
    {
        Object value ;
        try{
            value = Math.acos((double) x);
        }catch(Exception e)
        {
            value= Math.acos((long) x);
        }
        return (double) value;
    }/*
     * abs
     */
    public static double abs(Object x)
    {
        Object value ;
        try{
            value = Math.abs((double) x);
        }catch(Exception e)
        {
            value= Math.abs((long) x);
        }
        return (double) value;
    }
    /*
     * log10
     */
    public static double log10(Object x)
    {
        Object value ;
        try{
            value = Math.log10((double) x);
        }catch(Exception e)
        {
            value= Math.log10((long) x);
        }
        return (double) value;
    }


    /*
     * power
     */
    public static double power(Object x, int i)
    {
        Object value ;
        try{
            value = Math.pow((double) x,i);
        }catch(Exception e)
        {
            //Tempo fix should revise later
            try {
                value = Math.pow((long) x, i);
            }
            catch(Exception ex ) {
                value = Math.pow((Float) x, i);
            }
        }
        return (double) value;
    }


    /*
     * power
     */
    public static double bias(Object x, int i)
    {
        Object value ;
        try{
            value = (double) x+i;
        }catch(Exception e)
        {
            //Tempo fix should revise later
            try {
                value = (long) x+i;
            }
            catch(Exception ex ) {
                value = (float) x+i;
            }
        }
        return (double) value;
    }


    /*
     * exp
     */
    public static double exp(Object x) {
        Object value ;
        try{
            value = Math.exp((double) x);
        }catch(Exception e)
        {
            value= Math.exp((long) x);
        }
        return (double) value;

    }



    /*
     * factorial
     */
    public static double factorial(double x) {
        Double value = x;
        double a = CombinatoricsUtils.factorial(value.intValue());
        return a;
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
     * average
     */
    public static long average(double... numbers) {
        double sum = 0;
        int count = 0;
        for (double number : numbers) {
            sum+=number;
            count++;
        }
        if (count<=0)
        {
        	return 0;
        }
        return (long) sum/count;
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
