package io.je.utilities.math;

import io.je.utilities.unitconversion.JEUnitConverter;
import io.je.utilities.unitconversion.JEUnitConverter.UnitDefinition;

public class JEConverter {
	
	public static double convert(double value ,UnitDefinition initialUnit, UnitDefinition finalUnit)
	{
		 JEUnitConverter unitConverter = new JEUnitConverter(initialUnit.UNIT.getCategory() , initialUnit); 
	        return  unitConverter.convert(value, finalUnit);
	}
	
	
	

}
