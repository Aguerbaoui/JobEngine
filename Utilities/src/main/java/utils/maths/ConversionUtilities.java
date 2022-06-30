package utils.maths;

import utils.maths.UnitConverter.UnitDefinition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class ConversionUtilities {

    /*
     * convert between units
     */
    public static double convert(double value , UnitDefinition initialUnit, UnitDefinition finalUnit)
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
