package io.je.utilities.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JELogger {
	
	private static final Logger logger = LoggerFactory.getLogger(JELogger.class); 
	public static void info(String msg) {
		
		logger.info(msg);
	}
	

}
