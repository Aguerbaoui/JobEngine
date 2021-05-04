package io.je.utilities.logger;

import java.util.Date;

public class LogMessageFormat {
	
	
	public LogLevel logLevel ;
    public Object message ;
    public Date logDate ;
    public String module ;
    public LogCategory category ;
    public String projectId ;
    public String subModule ;
	
    
    public LogMessageFormat(LogLevel logLevel, Object message, Date logDate, String module, LogCategory category,
			String projectId, String subModule) {
		super();
		this.logLevel = logLevel;
		this.message = message;
		this.logDate = logDate;
		this.module = module;
		this.category = category;
		this.projectId = projectId;
		this.subModule = subModule;
	}

    
    
    
}
