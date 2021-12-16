package utils.log;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import utils.date.DateUtils;

/*
 * Class Responsible for logging 
 * A log request of level p in a logger with level q is enabled if p >= q. 
 * It assumes that levels are ordered. For the standard levels, we have   TRACE < DEBUG < INFO < WARN < ERROR
 */
public class LoggerUtils {

	private static Queue<LogMessage> queue = new LinkedList<>();
	protected static Logger logger = null;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	public static final Level CONTROL = Level.forName("CONTROL", 250);

	/*******************************
	 * TO BE DELETED
	 ****************************************************/

	protected LoggerUtils() {}
	
	public static Queue<LogMessage> getQueue() {
		return queue;
	}

	/*******************************************************************************************************/

	/*
	 * check if the message's log level is more specific than the logger's level
	 */

	protected static boolean logLevelIsEnabled(Level lvl) {
		if (lvl == logger.getLevel() || lvl == Level.ALL || lvl.isMoreSpecificThan(logger.getLevel())) {
			return true;
		}

		return false;
	}

	
	/*
	 * Trace log level
	 */
	public static void trace(String message) {
		logger.trace(message);

	
	}

	/*
	 * Control log level
	 */
	public static void control(String message) {
		logger.log(CONTROL, message);

		

	}

	/*
	 * Debug log level
	 */
	public static void debug(String message) {
		logger.debug(message);

		

	}

	public static void debugWithoutPublish(String message) {
//Log in file
		logger.debug(message);

	}


	/*
	 * Inform log level
	 */
	public static void info(String message) {
		// Log in file
		logger.info(message);

		
	}

	/*
	 * Error log level
	 */
	public static void error(String message) {
		// Log in file
		logger.error(message);

	}

	/*
	 * Warning log level
	 */
	public static void warn(String message) {
		// Log in file
		logger.warn(message);

	}

	// get Log message object for the logging service
	public static LogMessage getLogMessage(LogLevel logLevel, String message, LogCategory category, String projectId,
			LogSubModule subModule, String objectId) {
		String logDate = Instant.now().toString();

		return new LogMessage(logLevel, message, logDate, /* category, */ projectId, subModule, objectId);
	}

	// get Log message object for the logging service
	public static LogMessage getLogMessage(LogLevel logLevel, String message, LogCategory category, String projectId,
			LogSubModule subModule, String objectId, String blockName) {
		String logDate = Instant.now().toString();

		return new LogMessage(logLevel, message, logDate, /* category, */ projectId, subModule, objectId, blockName);
	}

	// get log string message
	/*
	 * public static String getLogStringText(String projectId, String subModule,
	 * String extraInfo, String... objectIds) { // In every log message, we have the
	 * porject id, module ( rule/ workflow / event / variable ), // related objects
	 * to it and the extra info explaining the action String msg = "[Project Id = "
	 * + projectId + "] [Submodule = " + subModule + "] "; if(objectIds != null) {
	 * for(int i = 0; i< objectIds.length; i++) { msg = msg + "[Object Id = " +
	 * objectIds[i] + " ] "; } } msg += extraInfo; return msg; }
	 */
	/***************************************************************************************************************/
	protected static Level getLogLevel(String level) {
		// ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF
		Level lvl = Level.DEBUG;
		switch (level.toUpperCase()) {
		case "ERROR":
			return Level.ERROR;
		case "DEBUG":
			break;
		case "INFO":
			return Level.INFO;
		case "INFORM":
			return Level.INFO;	
		case "WARN":
			return Level.WARN;
		case "TRACE":
			return Level.TRACE;
		case "CONTROL":
			return CONTROL;
		case "OFF":
			return Level.OFF;
		case "ALL":
			return Level.ALL;
		}
		return lvl;
	}

	public static void initLogger(String appName, String logPath, String level) {
		// TODO Remove the old logger context initialization (spring/activiti/drools)
		String pattern = "[%d] [%p]" + appName + " :: %m%n";
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

		builder.setStatusLevel(getLogLevel(level));
		builder.setConfigurationName(appName + "Logger");
		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(getLogLevel(level));
		// create a console appender
		AppenderComponentBuilder consoleAppender = builder.newAppender("Console", "CONSOLE").addAttribute("target",
				ConsoleAppender.Target.SYSTEM_OUT);
		consoleAppender.add(builder.newLayout("PatternLayout").addAttribute("pattern", pattern));
		rootLogger.add(builder.newAppenderRef("Console"));

		builder.add(consoleAppender);

		// create a rolling file appender
		LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout").addAttribute("pattern", pattern);
		ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
				.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10MB"));
		AppenderComponentBuilder appenderBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
				.addAttribute("fileName", logPath).addAttribute("filePattern", logPath + "-%d{MM-dd-yy-HH}.log.")
				.add(layoutBuilder).addComponent(triggeringPolicy);
		builder.add(appenderBuilder);

		rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
		builder.add(rootLogger);
		LoggerContext context = Configurator.initialize(builder.build());
		Configurator.shutdown(context);
		context = Configurator.initialize(builder.build());
		logger = context.getLogger("JobEngineLogger");
		// trace(JELogger.class, "Builder Logger initialized");
		logger.info(getInitialLogMessage(appName, level));

	}

	private static String getInitialLogMessage(String appName, String level) {
		return "\n======================================================================================\r\n"
				+ "==                                     " + appName + "                                     ==\r\n"
				+ "==                                 Version : 1.0.0                                  ==\r\n"
				+ "==                                 Build Date : " + LocalDateTime.now().format(formatter)
				+ "                            ==\r\n"
				+ "==                       Copyright � 2021 Integration Objects                       ==\r\n"
				+ "======================================================================================\r\n"
				+ "==                             Trace level : [ " + level + " ]                              ==\r\n"
				+ "======================================================================================";

	}

}
