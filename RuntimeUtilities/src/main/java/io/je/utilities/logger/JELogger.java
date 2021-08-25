package io.je.utilities.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import io.je.utilities.beans.JEData;
import io.je.utilities.config.Utility;
import io.je.utilities.time.JEDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;


/*
 * Class Responsible for logging 
 * A log request of level p in a logger with level q is enabled if p >= q. 
 * It assumes that levels are ordered. For the standard levels, we have   DEBUG < INFO < WARN < ERROR
 */
public class JELogger {

    private static Queue<LogMessage> queue = new LinkedList<>();
    private static  Logger logger = null;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    
  

    
    /*******************************TO BE DELETED ****************************************************/


    public static Queue<LogMessage> getQueue() {
        return queue;
    }

    
    
    /*******************************************************************************************************/

    /*
     * check if the message's log level is more specific than the logger's level 
     */
    
    private static boolean logLevelIsEnabled(Level lvl)
    {
    	if(lvl == logger.getLevel() || lvl == Level.ALL || lvl.isMoreSpecificThan(logger.getLevel()))
    	{
    		return true;
    	}

    	return false;
    }
    
    /*
    * Publish log message to SIOTHTracker
    * */
    private static void publishLogMessage(LogMessage logMessage) {
        //Debug > Inform > control > Error
        Level lvl = getLogLevel(logMessage.logLevel.toString());
        if(logLevelIsEnabled(lvl)) {
            ZMQLogPublisher.publish(logMessage);
        }
    }
    /*
     * Trace log level
     * */
    public static void trace(String message,  LogCategory category,
                             String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.trace( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.Control, message, category, projectId, subModule, objectId);
        publishLogMessage(logMessage);
    }

    /*
     * Debug log level
     * */
    public static void debug(String message,  LogCategory category,
                             String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.debug( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.Debug, message, category, projectId, subModule, objectId);
      //  publishLogMessage(logMessage);

    }

    /*
     * Inform log level
     * */
    public static void info(String message,  LogCategory category,
                            String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.info( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.Inform, message, category, projectId, subModule, objectId);
        publishLogMessage(logMessage);
    }

    /*
     * Error log level
     * */
    public static void error(String message,  LogCategory category,
                             String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.error( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.Error, message, category, projectId, subModule, objectId);
        publishLogMessage(logMessage);
    }

    /*
     * Warning log level
     * */
    public static void warn(String message,  LogCategory category,
                            String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.warn( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.Warning, message, category, projectId, subModule, objectId);
        publishLogMessage(logMessage);
    }

    // get Log message object for the logging service
     public static LogMessage getLogMessage(LogLevel logLevel, String message,  LogCategory category,
                                            String projectId, LogSubModule subModule, String objectId) {
         String logDate = JEDate.formatDate(LocalDateTime.now(), Utility.getSiothConfig().getDateFormat().replace(".fff", ".SSS"));
        		 
         return new LogMessage(logLevel, message, logDate, /*category,*/ projectId, subModule, objectId);
     }

    // get log string message
   /* public static String getLogStringText(String projectId, String subModule,  String extraInfo, String... objectIds) {
        // In every log message, we have the porject id, module ( rule/ workflow / event / variable ),
        // related objects to it and the extra info explaining the action
        String msg = "[Project Id = " + projectId + "] [Submodule = " + subModule + "] ";
        if(objectIds != null) {
            for(int i = 0; i< objectIds.length; i++) {
                msg = msg + "[Object Id = " + objectIds[i] + " ] ";
            }
        }
        msg += extraInfo;
        return msg;
    }*/
    /***************************************************************************************************************/
    private static Level getLogLevel(String level)
    {
        //ALL < TRACE/CONTROL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
    	Level lvl = Level.DEBUG;
    	switch(level.toUpperCase())
    	{
    	case "ERROR":
    		return Level.ERROR;
    	case "DEBUG":
    		break;    	
    	case "INFO":
    		return Level.INFO;
    	case "WARN":
    		return Level.WARN;
    	case "TRACE":
    		return Level.TRACE;
    	case "OFF":
    		return Level.OFF;
    	case "ALL":
    		return Level.ALL;
    	}
    	return lvl;
    }

    public static void initLogger(String appName, String logPath, String level) {
        //TODO Remove the old logger context initialization (spring/activiti/drools)
        String pattern = "[%d] [%p]" + appName + " :: %m%n";
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(getLogLevel(level));
        builder.setConfigurationName(appName + "Logger");
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(getLogLevel(level));
        // create a console appender
        AppenderComponentBuilder consoleAppender = builder.newAppender("Console", "CONSOLE").addAttribute("target",
                ConsoleAppender.Target.SYSTEM_OUT);
        consoleAppender.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", pattern));
        rootLogger.add(builder.newAppenderRef("Console"));

        builder.add(consoleAppender);

        // create a rolling file appender
        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", pattern);
        ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10MB"));
        AppenderComponentBuilder appenderBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
                .addAttribute("fileName", logPath)
                .addAttribute("filePattern", logPath + "-%d{MM-dd-yy-HH}.log.")
                .add(layoutBuilder)
                .addComponent(triggeringPolicy);
        builder.add(appenderBuilder);

        rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
        builder.add(rootLogger);
        LoggerContext context = Configurator.initialize(builder.build());
        Configurator.shutdown(context);
        context = Configurator.initialize(builder.build());
        logger = context.getLogger("JobEngineLogger");
        // trace(JELogger.class, "Builder Logger initialized");
        logger.info(getInitialLogMessage(appName,level));

    }

    
    private static String getInitialLogMessage(String appName, String level)
    {
    	return "\n======================================================================================\r\n"
    			+ "==                                     "+appName+"                                     ==\r\n"
    			+ "==                                 Version : 1.0.0                                  ==\r\n"
    			+ "==                                 Build Date : "+LocalDateTime.now().format(formatter)+"                            ==\r\n"
    			+ "==                       Copyright � 2020 Integration Objects                       ==\r\n"
    			+ "======================================================================================\r\n"
    			+ "==                             Trace level : [ "+level+" ]                              ==\r\n"
    			+ "======================================================================================";
    	
    	
    }

}
