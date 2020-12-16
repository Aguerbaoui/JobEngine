package io.je.utilities.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JELogger {

    private static final Logger logger = LogManager.getLogger(JELogger.class);

    public static void info(Class<?> clazz, String msg) {
        logger.info(clazz.toString() + msg);
    }

    public static void error(Class<?> clazz, String msg) {
        logger.info(clazz.toString() + msg);


    }


}
