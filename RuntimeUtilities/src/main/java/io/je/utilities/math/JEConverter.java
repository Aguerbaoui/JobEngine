package io.je.utilities.math;

import utils.maths.ConversionUtilities;
import utils.maths.UnitConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JEConverter {
	
	/*
	 * convert between units
	 */
	public static double convert(double value , UnitConverter.UnitDefinition initialUnit, UnitConverter.UnitDefinition finalUnit)
	{
		UnitConverter unitConverter = new UnitConverter(initialUnit.UNIT.getCategory() , initialUnit);
	        return  unitConverter.convert(value, finalUnit);
	}
	
	
	public static Date convertTypeDate(String dateFormat, String dateAsString)
	{
		Date date1=null;
		try {
			date1=new SimpleDateFormat(dateFormat).parse(dateAsString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return date1;

	}

}
