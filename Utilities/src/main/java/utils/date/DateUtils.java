package utils.date;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private DateUtils() {}
    public static LocalDateTime getCurrentTime()
    {
        return LocalDateTime.now();
    }

    private static DateTimeFormatter formatter;

    public static void setFormatter(String format) {
        formatter = DateTimeFormatter.ofPattern(format);
    }
    /*
     * returns a String of the current time in the format specified
     */
    public static String getCurrentTimeAsString(String formatToApply)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatToApply);
        return LocalDateTime.now().format(formatter);

    }

    /*
     * return a string of the specified date into the specific format
     */
    public static String formatDate(LocalDateTime date, String timeFormat)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        return date.format(formatter);

    }

    /*
     * input : String in format "timeFormat"
     * output : String in format "formatToapply"
     */
    public static String formatDateString(String date, String timeFormat,String formatToApply)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatToApply);
        return getTimeFromString(date,timeFormat).format(formatter);

    }


    public static String parseUTCStringToLocalTimeString(String utcStr)
    {
        if (utcStr == null) return null;

    	return LocalDateTime.ofInstant(Instant.parse(utcStr), ZoneOffset.systemDefault()).toString();
    }
    
    
    /*
     * converts a date String to a LocalDateTime instance
     */
    public static LocalDateTime getTimeFromString(String timeAsString,String timeFormat)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);

        return LocalDateTime.parse(timeAsString, formatter);
    }

    public static String formatDateToSIOTHFormat(LocalDateTime date) {
        return date.format(formatter);
    }
}
