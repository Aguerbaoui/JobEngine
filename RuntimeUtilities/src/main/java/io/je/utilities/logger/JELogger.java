package io.je.utilities.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

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


    /*
     * log level 1 : debug
     */
    public static void debug(Class<?> clazz, String msg) {

        logger.debug(clazz.getName() +" : "+ msg.trim());
        
        
    }

    /*
     * log level 1 : debug
     */
    public static void debug(String msg) {

        logger.debug(msg.trim());

    }

    public static Queue<LogMessage> getQueue() {
        return queue;
    }

    /*
     * log level 2 : info
     */
    public static void info(Class<?> clazz, String msg) {
       // logger.info(logger.getName() + ": " + logger);

        logger.info( msg.trim());
    }

    public static void info( String msg) {

        logger.info( msg.trim());
    }
    /*
     * log level 3 : warn
     */
    public static void warning(Class<?> clazz, String msg) {
        logger.warn( msg.trim());
    }
    
    
    /*
     * log level 4 : error
     */
    public static void error(Class<?> clazz, String msg) {

        logger.error(clazz.getName() +" : "+  msg.trim());

    }

    /*
     * log level 4 : error
     */
    public static void error(String msg) {

        logger.error(  msg.trim());


    }

    
    
    /*******************************************************************************************************/


    /*
     * Trace log level
     * */
    public static void trace(String message,  LogCategory category,
                             String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.trace( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.CONTROL, message, category, projectId, subModule, objectId);
        ZMQLogPublisher.publish(logMessage);
    }

    /*
     * Debug log level
     * */
    public static void debug(String message,  LogCategory category,
                             String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.debug( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.DEBUG, message, category, projectId, subModule, objectId);
        ZMQLogPublisher.publish(logMessage);

    }

    /*
     * Inform log level
     * */
    public static void info(String message,  LogCategory category,
                            String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.info( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.INFORM, message, category, projectId, subModule, objectId);
        ZMQLogPublisher.publish(logMessage);
    }

    /*
     * Error log level
     * */
    public static void error(String message,  LogCategory category,
                             String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.error( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.ERROR, message, category, projectId, subModule, objectId);
        ZMQLogPublisher.publish(logMessage);
    }

    /*
     * Warning log level
     * */
    public static void warn(String message,  LogCategory category,
                            String projectId, LogSubModule subModule, String objectId) {
        //Log in file
        logger.warn( message);

        //Log in logging service
        LogMessage logMessage = getLogMessage(LogLevel.WARNING, message, category, projectId, subModule, objectId);
        ZMQLogPublisher.publish(logMessage);
    }

    // get Log message object for the logging service
     public static LogMessage getLogMessage(LogLevel logLevel, String message,  LogCategory category,
                                            String projectId, LogSubModule subModule, String objectId) {
         String logDate = LocalDateTime.now().toString();
         return new LogMessage(logLevel, message, logDate, category, projectId, subModule, objectId);
     }

    // get log string message
    public static String getLogStringText(String projectId, String subModule,  String extraInfo, String... objectIds) {
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
    }
    /***************************************************************************************************************/
    private static Level getLogLevel(String level)
    {
        //ALL < TRACE/CONTROL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
    	Level lvl = Level.DEBUG;
    	switch(level)
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
        info(JELogger.class, getInitialLogMessage(appName,level) );

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
