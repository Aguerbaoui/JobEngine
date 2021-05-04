package io.je.utilities.logger;

import java.util.Date;

public class LoggingService {
	
	
	
	public static void publish ( String projectId, LogLevel logLevel,Date logDate, LogCategory category, String subModule, Object message  )
	{
		LogMessageFormat msg = new LogMessageFormat(logLevel, message, logDate, "JobEngine", category, projectId, subModule);
		ZMQLogPublisher.publish(msg);
	}
	
	
public static void main(String[] args) {
	
	LoggingService.publish("123", LogLevel.Inform, new Date(), LogCategory.DesignMode, "Rule", "Rule was added");
	}

}
