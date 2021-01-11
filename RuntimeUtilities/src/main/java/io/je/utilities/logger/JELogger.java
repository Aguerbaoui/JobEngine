package io.je.utilities.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.util.Map;


/*
 * Class Responsible for logging 
 * A log request of level p in a logger with level q is enabled if p >= q. 
 * It assumes that levels are ordered. For the standard levels, we have   DEBUG < INFO < WARN < ERROR
 */
public class JELogger {

    private static  Logger logger = null;

    private static String fileName = "D:\\JobEngine.log";
    private static String pattern = "%d %p %c [%t] %m%n";
    private static boolean initialized = false;
    public static void initLogger() {
        //TODO Remove the old logger context initialization (spring/activiti/drools)
        if(!initialized) {
            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

            builder.setStatusLevel(Level.OFF);
            builder.setConfigurationName("JobEngineLogger");

            // create a console appender
            /*AppenderComponentBuilder appenderBuilder = builder.newAppender("Console", "CONSOLE").addAttribute("target",
                    ConsoleAppender.Target.SYSTEM_OUT);
            appenderBuilder.add(builder.newLayout("PatternLayout")
                    .addAttribute("pattern", pattern));
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.INFO);
            rootLogger.add(builder.newAppenderRef("Console"));

            builder.add(appenderBuilder);*/

            // create a rolling file appender
            LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                    .addAttribute("pattern", pattern);
            ComponentBuilder triggeringPolicy = builder.newComponent("Policies")
                    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "1KB"));
            AppenderComponentBuilder appenderBuilder = builder.newAppender("LogToRollingFile", "RollingFile")
                    .addAttribute("fileName", fileName)
                    .addAttribute("filePattern", fileName+"-%d{MM-dd-yy-HH-mm-ss}.log.")
                    .add(layoutBuilder)
                    .addComponent(triggeringPolicy);
            builder.add(appenderBuilder);
            RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.INFO);
            rootLogger.add(builder.newAppenderRef("LogToRollingFile"));
            builder.add(rootLogger);
            LoggerContext context = Configurator.initialize(builder.build());
            Configurator.shutdown(context);
            context = Configurator.initialize(builder.build());
            logger = context.getLogger("JobEngineLogger");
            //Map<String, Appender> map = context.getLogger("JobEngineLogger").getAppenders();
            initialized = true;
        }
    }
    public static void info(Class<?> clazz, String msg) {
        if(!initialized) initLogger();
        logger.info(clazz.toString() + msg);
    }

    public static void error(Class<?> clazz, String msg) {
        if(!initialized) initLogger();
        logger.info(clazz.toString() + msg);


    }

   /* public static void  main(String[] args) {
        initLogger();
        logger.info("he");
        //logger.debug("he");
    }*/



}
