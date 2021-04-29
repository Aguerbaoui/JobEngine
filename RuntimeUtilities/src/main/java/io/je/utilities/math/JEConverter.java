package io.je.utilities.math;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.je.utilities.unitconversion.JEUnitConverter;
import io.je.utilities.unitconversion.JEUnitConverter.UnitDefinition;

public class JEConverter {
	
	/*
	 * convert between units
	 */
	public static double convert(double value ,UnitDefinition initialUnit, UnitDefinition finalUnit)
	{
		 JEUnitConverter unitConverter = new JEUnitConverter(initialUnit.UNIT.getCategory() , initialUnit); 
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
