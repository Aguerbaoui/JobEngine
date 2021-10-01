package io.je.utilities.log;

import java.time.LocalDateTime;

import org.apache.logging.log4j.Level;

import io.je.utilities.log.JELogger;
import utils.date.DateUtils;
import utils.log.LogCategory;
import utils.log.LogLevel;
import utils.log.LogMessage;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;

public class JELogger extends LoggerUtils {
	
	/*
	 * Publish log message to SIOTHTracker
	 */
	private static void publishLogMessage(LogMessage logMessage) {
		// Debug > Inform > control > Error
		Level lvl = getLogLevel(logMessage.logLevel.toString());
		if (logLevelIsEnabled(lvl)) {
			ZMQLogPublisher.publish(logMessage);
		}
	}

	/*
	 * Trace log level
	 */
	public static void trace(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId) {
		trace(message);

	}

	/*
	 * Control log level
	 */
	public static void control(String message, LogCategory category, String projectId, LogSubModule subModule,
							 String objectId) {
	

		// Log in file
		control(message);
		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Control, message, category, projectId, subModule, objectId);
		publishLogMessage(logMessage);

	}

	/*
	 * Debug log level
	 */
	public static void debug(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId) {
		// Log in file
		debug(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Debug, message, category, projectId, subModule, objectId);
		publishLogMessage(logMessage);

	}

	public static void debugWithoutPublish(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId) {
//Log in file
		debug(message);

//Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Debug, message, category, projectId, subModule, objectId);
		//publishLogMessage(logMessage);

	}

	/*
	 * Debug log level
	 */
	public static void debug(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId, String blockName) {
		// Log in file
		debug(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Debug, message, category, projectId, subModule, objectId,
				blockName);
		// publishLogMessage(logMessage);

	}

	/*
	 * Inform log level
	 */
	public static void info(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId) {
		// Log in file
		info(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Inform, message, category, projectId, subModule, objectId);
		publishLogMessage(logMessage);
	}

	/*
	 * Block Inform log
	 */
	public static void info(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId, String blockName) {
		// Log in file
		info(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Inform, message, category, projectId, subModule, objectId,
				blockName);
		publishLogMessage(logMessage);
	}

	/*
	 * Error log level
	 */
	public static void error(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId) {
		// Log in file
		error(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Error, message, category, projectId, subModule, objectId);
		publishLogMessage(logMessage);
	}

	/*
	 * Error log level
	 */
	public static void error(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId, String blockName) {
		// Log in file
		error(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Error, message, category, projectId, subModule, objectId,
				blockName);
		publishLogMessage(logMessage);
	}

	/*
	 * Warning log level
	 */
	public static void warn(String message, LogCategory category, String projectId, LogSubModule subModule,
			String objectId) {
		// Log in file
		warn(message);

		// Log in logging service
		LogMessage logMessage = getLogMessage(LogLevel.Warning, message, category, projectId, subModule, objectId);
		publishLogMessage(logMessage);
	}

	// get Log message object for the logging service
	public static LogMessage getLogMessage(LogLevel logLevel, String message, LogCategory category, String projectId,
			LogSubModule subModule, String objectId) {
		String logDate = DateUtils.formatDate(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss.SSS");

		return new LogMessage(logLevel, message, logDate, /* category, */ projectId, subModule, objectId);
	}

	// get Log message object for the logging service
	public static LogMessage getLogMessage(LogLevel logLevel, String message, LogCategory category, String projectId,
			LogSubModule subModule, String objectId, String blockName) {
		String logDate = DateUtils.formatDate(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss.SSS");

		return new LogMessage(logLevel, message, logDate, /* category, */ projectId, subModule, objectId, blockName);
	}

}
