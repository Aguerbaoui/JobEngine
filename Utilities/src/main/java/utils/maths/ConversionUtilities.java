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
    public static Date convertTypeDate(String dateFormat, String dateAsString) throws ParseException {
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat(dateFormat).parse(dateAsString);
        } catch (ParseException e) {
            LoggerUtils.logException(e);
            // FIXME log with JELogger
            // throw e; TODO Issue 247 : throw exception stop Drools rule engine, need to call JELogger to send error to UI instead
        }
        return date1;

    }

    public static String convertIfBoolean(String var) {
        if (var.equalsIgnoreCase("true")) {
            return "1";
        } else if (var.equalsIgnoreCase("false")) {
            return "0";
        }
        if (var.isEmpty() || var.isBlank()) {
            return "";
        }
        return var;
    }

}
