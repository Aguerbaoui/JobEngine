package io.je.utilities.time;

import io.je.utilities.config.Utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JEDate {

	
	public static LocalDateTime getCurrentTime()
	{
		return LocalDateTime.now();
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.getSiothConfig().getDateFormat());

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
