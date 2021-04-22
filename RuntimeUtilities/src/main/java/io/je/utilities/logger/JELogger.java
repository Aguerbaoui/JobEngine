package io.je.utilities.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.sql.Timestamp;
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

    private static Queue<String> queue = new LinkedList<>();
    private static  Logger logger = null;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");


    /*
    * Trace log level
    * */
     public static void trace(Class<?> clazz, String msg) {
         synchronized (queue) {
             queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg.trim());
         }

        logger.info( msg.trim());

    }

    public static void trace(String msg) {
        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg.trim());
        }

        logger.info( msg.trim());

    }
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

    public static Queue<String> getQueue() {
        return queue;
    }

    /*
     * log level 2 : info
     */
    public static void info(Class<?> clazz, String msg) {
        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg.trim());
        }
       // logger.info(logger.getName() + ": " + logger);

        logger.info( msg.trim());
    }

    public static void info( String msg) {
        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg.trim());
        }

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

        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg.trim());
        }
        logger.error(clazz.getName() +" : "+  msg.trim());


    }

    /*
     * log level 4 : error
     */
    public static void error(String msg) {

        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg.trim());
        }
        logger.error(  msg.trim());


    }


    private static Level getLogLevel(String level)
    {
    	Level lvl = Level.DEBUG;
    	switch(level)
    	{
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

    public static void initBuilderLogger(String jeBuilderLogPath, String level) {
        //TODO Remove the old logger context initialization (spring/activiti/drools)

    	String pattern = "[%d] [%p] JEBuilder :: %m%n";
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(Level.OFF);
        builder.setConfigurationName("JobEngineBuilderLogger");
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
                .addAttribute("fileName", jeBuilderLogPath)
                .addAttribute("filePattern", jeBuilderLogPath + "-%d{MM-dd-yy-HH}.log.")
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
        info(JELogger.class, getInitialLogMessage("JEBuilder",level) );

    }

    public static void initRunnerLogger(String jeRunnerLogPath, String level) {
        //TODO Remove the old logger context initialization (spring/activiti/drools)
    	String pattern = "[%d] [%p] JERunner :: %m%n";

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(Level.OFF);
        builder.setConfigurationName("JobEngineRunnerLogger");
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
                .addAttribute("fileName", jeRunnerLogPath)
                .addAttribute("filePattern", jeRunnerLogPath + "-%d{MM-dd-yy-HH}.log.")
                .add(layoutBuilder)
                .addComponent(triggeringPolicy);
        builder.add(appenderBuilder);

        rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
        builder.add(rootLogger);
        LoggerContext context = Configurator.initialize(builder.build());
        Configurator.shutdown(context);
        context = Configurator.initialize(builder.build());
        logger = context.getLogger("JobEngineLogger");
        //trace(JELogger.class, "Runtime Logger initialized");
        info(JELogger.class, getInitialLogMessage("JERunner",level) );


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
