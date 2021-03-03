package io.je.utilities.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.sql.Timestamp;
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

    private static final String pattern = "%d %p %c [%t] %m%n";

    /*
    * Trace log level
    * */
     public static void trace(Class<?> clazz, String msg) {
         synchronized (queue) {
             queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg);
         }

        logger.trace(clazz.toString() +" : "+ msg);

    }
    /*
     * log level 1 : debug
     */
    public static void debug(Class<?> clazz, String msg) {

        logger.debug(clazz.toString() +" : "+ msg);
        
    }


    public static Queue<String> getQueue() {
        return queue;
    }

    /*
     * log level 2 : info
     */
    public static void info(Class<?> clazz, String msg) {
        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg);
        }
        logger.info(clazz.toString() +" : " + msg);
    }

    public static void info( String msg) {
        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg);
        }

        logger.info( msg);
    }
    /*
     * log level 3 : warn
     */
    public static void warning(Class<?> clazz, String msg) {
        logger.warn(clazz.toString() + msg);
    }
    
    
    /*
     * log level 4 : error
     */
    public static void error(Class<?> clazz, String msg) {

        synchronized (queue) {
            queue.add(new Timestamp(System.currentTimeMillis()) + " " + msg);
        }
        logger.error(clazz.toString() + msg);


    }

    public static void initBuilderLogger(String jeBuilderLogPath) {
        //TODO Remove the old logger context initialization (spring/activiti/drools)


        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(Level.OFF);
        builder.setConfigurationName("JobEngineBuilderLogger");
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ALL);
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
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10KB"));
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
        trace(JELogger.class, "Builder Logger initialized");
    }

    public static void initRunnerLogger(String jeRunnerLogPath) {
        //TODO Remove the old logger context initialization (spring/activiti/drools)

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.setStatusLevel(Level.OFF);
        builder.setConfigurationName("JobEngineRunnerLogger");
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.ALL);
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
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "10KB"));
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
        trace(JELogger.class, "Runtime Logger initialized");

    }

}
