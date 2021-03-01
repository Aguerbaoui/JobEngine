package io.je.utilities.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

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
    private static  Logger logger = LogManager.getLogger(JELogger.class);
  /*  private static int logLevel = 2;
    static LoggerContext context = (LoggerContext) LogManager.getContext(false);
	static Configuration config = context.getConfiguration();
	static LoggerConfig rootConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    public static void setLogLevel(int logLevel)
    {
    	
    	switch(logLevel)
    	{case 1:
    	rootConfig.setLevel(Level.DEBUG);
    	break;
    	}
    	rootConfig.setLevel(Level.DEBUG);

    	context.updateLoggers();
    }*/

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


}
