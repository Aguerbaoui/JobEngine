package utils.maths;

import utils.log.LoggerUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversionUtilities {

    /*
     * convert between units
     */
    // DO NOT REMOVE, USED in .drl (Drools)
    public static double convert(double value, UnitDefinition initialUnit, UnitDefinition finalUnit) {
        UnitConverter unitConverter = new UnitConverter(initialUnit.UNIT.getCategory(), initialUnit);
        return unitConverter.convert(value, finalUnit);
    }

    // DO NOT REMOVE, USED in .drl (Drools)
    public static Date convertTypeDate(String dateFormat, String dateAsString) {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat(dateFormat).parse(dateAsString);
        } catch (ParseException e) {
            // FIXME may be log with JELogger
            LoggerUtils.logException(e);
        }
        return date1;

    }
}
