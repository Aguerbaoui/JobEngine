package utils.maths;

import org.apache.commons.math3.util.CombinatoricsUtils;

public class MathUtilities {

	public static int decimalPrecision = 3;
	
	
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
            value= Math.log((float) x);
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
            value= Math.ceil((float) x);
        }
        return (double) value;
    }





    /*
     * truncate
     */
    public static double truncate(Object x)
    {
        double value ;
        try{
            value = Math.round((double) x);
        }catch(Exception e)
        {
            value= Math.round((float) x);
        }
        return  value;
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
            value= Math.floor((float) x);
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
            value= Math.sin((float) x);
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
            value= Math.cos((float) x);
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
            value= Math.asin((float) x);
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
            value= Math.atan((float) x);
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
            value= Math.tan((float) x);
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
            value= Math.pow((float) x,2);
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
            value= Math.sqrt((float) x);
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
            value= Math.acos((float) x);
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
            value= Math.abs((float) x);
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
            value= Math.log10((float) x);
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
            //Temp fix should revise later
            try {
                value = Math.pow((double) x, i);
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
                value = (double) x+i;
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
            value= Math.exp((float) x);
        }
        return (double) value;

    }



    /*
     * factorial
     */
    public static double factorial(double x) {
        Double value = x;
        if(value.intValue()>0 && value <=20) {
        	return CombinatoricsUtils.factorial(value.intValue());

        }else {
        	return 1;

        }
    }



    /*
     * change sign
     */
    public static double changeSign(Object x) {
        return  -(double)x;

    }

    /*
     * change sign
     */
    public static double changeSign(float x) {
        return  -(double)x;

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
     * highLimiting returns min ( limitValue, max(Inputs..) )
     */
    public static double highLimiting(double limit, double... numbers) {
        double max = maximum(numbers);

        return (double) Math.min(limit, max);

    }

    /*
     * low Limiting returns min ( limitValue, min(Inputs..) )
     */
    public static double lowLimiting(double limit, double... numbers) {
        double min = minimum(numbers);

        return (double) Math.min(limit, min);

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
            sum+=number;
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
            sum+=number;
            count++;
        }
        if (count<=0)
        {
        	return 0;
        }
        return (double) sum/count;
    }
    /*
     * multiply
     */
    public static double multiply(double... numbers) {
        double result = 1;
        for (double number : numbers) {
            result*=number;
        }
        return (double) result;
    }

    /*
     * subtract
     */
    public static double subtract(double a , double b) {

        return (double) (a-b);
    }



    /*
     * divide
     */
    public static double divide(double a , double b) {

        return  a/b;
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




}
